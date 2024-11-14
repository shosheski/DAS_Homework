#filters.check_last_date
import os
import pandas as pd

def get_last_date(issuer):
    filename = f"data/{issuer}.csv"
    if not os.path.exists(filename):
        print(f"File not found for issuer {issuer}. Returning default None.")
        return None

    try:
        df = pd.read_csv(filename)
        last_date = df["Date"].iloc[-1]
        return last_date
    except (IndexError, KeyError, pd.errors.EmptyDataError) as e:
        print(f"Error reading last date for issuer {issuer}: {e}")
        return None