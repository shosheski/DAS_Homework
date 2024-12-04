import os
import pandas as pd
from datetime import datetime

LAST_CHECKED_FILE = 'last_checked_date.csv'

def get_last_checked_date():
    if not os.path.exists(LAST_CHECKED_FILE):
        print("No last checked date found. Returning default date (10 years ago).")
        return datetime.now() - pd.DateOffset(years=10)

    try:
        df = pd.read_csv(LAST_CHECKED_FILE)
        if not df.empty:
            last_checked_str = df['LastChecked'].iloc[0]
            return datetime.strptime(last_checked_str, "%d.%m.%Y")
        else:
            print("Last checked date is missing in the file. Returning default date.")
            return datetime.now() - pd.DateOffset(years=10)
    except Exception as e:
        print(f"Error reading the last checked date: {e}")
        return datetime.now() - pd.DateOffset(years=10)

def update_last_checked_date(date_obj):
    try:
        df = pd.DataFrame({'LastChecked': [date_obj.strftime("%d.%m.%Y")]})
        df.to_csv(LAST_CHECKED_FILE, index=False)
        print(f"Updated last checked date to: {date_obj.strftime('%d.%m.%Y')}")
    except Exception as e:
        print(f"Error updating the last checked date: {e}")