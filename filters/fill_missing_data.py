#filters.fill_missing_data.py
import pandas as pd
import requests
from datetime import datetime, timedelta
from utils.transform_utils import (transform_date_to_string,
                                       get_soup_df, get_response_with_session)
from utils.csv_handler import save_to_csv

def fill_missing_data(issuer_dates):
    now = datetime.now()
    for issuer, last_date in issuer_dates.items():
        if last_date is None:
            print(f"Warning: No last date found for issuer {issuer}. Using a default date.")
            last_date_obj = now - timedelta(days=365 * 10)
        else:
            last_date_obj = datetime.strptime(last_date, "%d.%m.%Y")

        if last_date_obj.date() != now.date():
            update_missing_data(issuer, last_date_obj)
    return issuer_dates


def update_missing_data(issuer, last_date):
    new_data = pd.DataFrame(columns=[
        "Date", "Price of last transaction", "Max.", "Min.", "Average price",
        "%prom.", "Quantity", "BEST turnover in denars", "Total turnover in denars"
    ])
    session = requests.Session()
    now = datetime.now()
    years_needed = int((now - last_date).days / 364) + 1

    for i in range(years_needed):
        url = generate_url(issuer, i, last_date)
        soup = get_response_with_session(url, session)
        temp_data = get_soup_df(soup)

        if temp_data is None:
            break

        new_data = pd.concat([new_data, temp_data], ignore_index=True)

    new_data = new_data.drop_duplicates(subset=["Date"])

    new_data["Date"] = pd.to_datetime(new_data["Date"], format="%d.%m.%Y")
    new_data = new_data.sort_values(by="Date")

    new_data.columns = [
        "Датум", "Цена на последна трансакција", "Мак.", "Мин.", "Просечна цена",
        "%пром.", "Количина", "Промет во БЕСТ во денари", "Вкупен промет во денари"
    ]

    new_data["Промет во БЕСТ во денари"] = pd.to_numeric(new_data["Промет во БЕСТ во денари"].str.replace(',', '').str.strip(), errors='coerce')
    new_data["Вкупен промет во денари"] = pd.to_numeric(new_data["Вкупен промет во денари"].str.replace(',', '').str.strip(), errors='coerce')

    new_data = new_data[
        (new_data["Промет во БЕСТ во денари"] != 0) |
        (new_data["Вкупен промет во денари"] != 0)
    ]

    new_data["Промет во БЕСТ во денари"] = new_data["Промет во БЕСТ во денари"].fillna(0)
    new_data["Вкупен промет во денари"] = new_data["Вкупен промет во денари"].fillna(0)

    new_data["Датум"] = new_data["Датум"].dt.strftime('%d.%m.%Y')

    if not new_data.empty:
        save_to_csv(issuer, new_data)

def generate_url(issuer, i, last_date):
    url = f'https://www.mse.mk/mk/stats/symbolhistory/{issuer.lower()}'
    days = 364
    now = datetime.now()
    offset_days = days * i
    to_date = now - timedelta(days=offset_days)
    from_date = to_date - timedelta(days=days)

    if from_date < last_date:
        from_date = last_date + timedelta(days=1)

    return url + f'?FromDate={transform_date_to_string(from_date)}&ToDate={transform_date_to_string(to_date)}&Code={issuer}'