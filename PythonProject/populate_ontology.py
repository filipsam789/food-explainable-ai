import pandas as pd
from owlready2 import *

# === Ontology ===
onto = get_ontology("http://example.org/food_fraud.owl")

with onto:
    class Area(Thing): pass
    class Year(Thing): pass
    class Item(Thing): pass
    class Indicator(Thing): pass
    class IndicatorValue(Thing): pass

    class observedInArea(ObjectProperty):
        domain = [IndicatorValue]; range = [Area]
    class observedInYear(ObjectProperty):
        domain = [IndicatorValue]; range = [Year]
    class observedForItem(ObjectProperty):
        domain = [IndicatorValue]; range = [Item]
    class measuresIndicator(ObjectProperty):
        domain = [IndicatorValue]; range = [Indicator]

    class hasValue(DataProperty, FunctionalProperty):
        domain = [IndicatorValue]; range = [float]

# --- Helper Functions ---

def get_or_create(cache, cls, name):
    """
    Check if we already created this instance. If not, create it.
    Avoids duplicates in the ontology.
    """
    if pd.isna(name) or not str(name).strip():
        return None
    if name not in cache:

        cache[name] = cls(name.replace(" ", "_").replace(",", ""))
    return cache[name]

def parse_float(val):
    """
    Safely convert a string or number to a float.
    Returns None if it fails.
    """
    try:
        return float(str(val).replace(",", "").strip())
    except:
        return None

# --- Load dataset ---
df = pd.read_csv("data/data.csv")

# These caches remember what we've already created
area_cache, year_cache, item_cache, indicator_cache = {}, {}, {}, {}

# --- Define indicators
macro_indicators = [
    "Food Inflation Rate",
    "Raw GDP",
    "GDP Growth Rate",
    "Overall Inflation Rate",
    "Import Rate",
    "Export Rate",
    "Unemployment Rate",
    "Official Exchange Rate"
]

item_indicator = "Item Price Per Tonne"

# --- Process each row in the CSV ---
for _, row in df.iterrows():
    # Create or reuse instances for area, year, and item
    area = get_or_create(area_cache, onto.Area, row["Area"])
    year = get_or_create(year_cache, onto.Year, str(row["Year"]))
    item = get_or_create(item_cache, onto.Item, row["Item"]) if pd.notna(row["Item"]) else None

    # --- Macro indicators ---
    for col in macro_indicators:
        if col not in row or pd.isna(row[col]):
            continue
        val = parse_float(row[col])
        if val is None:
            continue

        # Create or reuse the indicator
        indicator = get_or_create(indicator_cache, onto.Indicator, col)
        # Create a new value for this area/year/indicator
        ind_val = IndicatorValue(f"{row['Area']}_{row['Year']}_{col}".replace(" ", "_"))
        ind_val.observedInArea = [area]
        ind_val.observedInYear = [year]
        ind_val.measuresIndicator = [indicator]
        ind_val.hasValue = val  # Store the numeric value

    # --- Item-specific indicator ---
    if item and item_indicator in row and pd.notna(row[item_indicator]):
        val = parse_float(row[item_indicator])
        if val is None:
            continue

        indicator = get_or_create(indicator_cache, onto.Indicator, item_indicator)
        ind_val = IndicatorValue(f"{row['Area']}_{row['Year']}_{row['Item']}_Price".replace(" ", "_").replace(",", ""))
        ind_val.observedInArea = [area]
        ind_val.observedInYear = [year]
        ind_val.observedForItem = [item]
        ind_val.measuresIndicator = [indicator]
        ind_val.hasValue = val

# --- Save ontology ---
onto.save(file="food_fraud.owl", format="rdfxml")
print("Ontology saved to food_fraud.owl")
