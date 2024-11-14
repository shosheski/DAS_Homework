#utils.csv_handler.py
import os
import pandas as pd

def save_to_csv(issuer, data):
    print(f"Received data for issuer {issuer}: {data}")

    if not isinstance(data, pd.DataFrame):
        print(f"Error: Invalid data format for issuer {issuer}. Expected a DataFrame.")
        return

    filtered_data = data[
        (data["Промет во БЕСТ во денари"] != 0) | (data["Вкупен промет во денари"] != 0)
    ]

    print(f"Filtered data for issuer {issuer}: {filtered_data}")

    if filtered_data.empty:
        print(f"No valid data to save for issuer {issuer}. Skipping...")
        return

    output_dir = f"database/{issuer}.csv"
    os.makedirs(os.path.dirname(output_dir), exist_ok=True)

    try:
        filtered_data.to_csv(output_dir, index=False)
        print(f"Data saved to {output_dir} for issuer {issuer}.")
    except Exception as e:
        print(f"Error saving data for issuer {issuer} to CSV: {e}")