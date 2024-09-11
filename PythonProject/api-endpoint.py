import pandas as pd
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import shap
import joblib
import matplotlib.pyplot as plt
import io
import base64
import pyppeteer
import asyncio
from PIL import Image

app = FastAPI()

# Define a model for input data
class PlotRequest(BaseModel):
    type_model: str
    features: list
    plot_type: str

def generate_shap_values(model,features,x_test):
    explainer = shap.Explainer(model)
    shap_values = explainer(x_test)
    feature_indices = [x_test.columns.tolist().index(f) for f in features]
    return shap_values,feature_indices,explainer.expected_value[1]

# Helper function to convert plots to base64 strings
def plot_to_base64(fig):
    buf = io.BytesIO()
    fig.savefig(buf, format="png")
    buf.seek(0)
    img_str = base64.b64encode(buf.read()).decode('utf-8')
    plt.close(fig)
    return img_str


def html_to_image_stream(html_content):
    browser =  pyppeteer.launch(headless=True)
    page = browser.newPage()
    page.setContent(html_content)

    # Capture screenshot
    screenshot = page.screenshot()
    browser.close()

    # Convert PNG to a byte stream
    image_stream = io.BytesIO(screenshot)
    return image_stream

# Function for generating summary plot
def generate_summary_plot(shap_values, x_test,feature_indices,features):
    fig, ax = plt.subplots()
    filtered_features=shap_values[:,feature_indices,0]
    shap.summary_plot(filtered_features, x_test[features], show=False)
    return plot_to_base64(fig)


# Function for generating dependence plot
def generate_dependence_plot(shap_values, x_test, feature_indices):
    fig, ax = plt.subplots()
    shap.dependence_plot(1, shap_values.values[:, :, 0], x_test, ax=ax, show=False)
    return plot_to_base64(fig)


# Function for generating force plot
def generate_force_plot(shap_values, base_value, feature_indices,x_test):

    #filtered_features=shap_values.values[:,feature_indices]
    fig, ax = plt.subplots()
    shap_values_array = shap_values.values[:500,:,1]

    shap.initjs()
    shap.force_plot(base_value, shap_values_array, x_test, show=False)



# Function for generating waterfall plot
def generate_waterfall_plot(shap_values,feature_indices):
    fig, ax = plt.subplots()
    filtered_values = shap_values[25, feature_indices, 0]
    shap.waterfall_plot(filtered_values, show=False)
    return plot_to_base64(fig)

@app.post("/visualization")
async def generate_plot(request: PlotRequest):
    model = joblib.load(f"models/{request.type_model}_model.pkl")
    x_test = pd.read_csv(f"data/x_test_{request.type_model}.csv")

    shap_values,feature_indices,base_value=generate_shap_values(model,request.features,x_test)
    try:
        if request.plot_type == "summary_plot":
            plot_image = generate_summary_plot(shap_values, x_test,feature_indices,request.features)
        elif request.plot_type == "dependence_plot":
            plot_image = generate_dependence_plot(shap_values, x_test, request.features)
        elif request.plot_type == "force_plot":
            plot_image = generate_force_plot(shap_values, base_value,feature_indices,x_test)
        elif request.plot_type == "waterfall_plot":
            plot_image = generate_waterfall_plot(shap_values,feature_indices)
        else:
            raise HTTPException(status_code=400, detail="Invalid plot type")

        return plot_image

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating plot: {str(e)}")

