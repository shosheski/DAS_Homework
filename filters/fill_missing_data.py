import pandas as pd
import requests
from datetime import datetime, timedelta
from filters.check_last_date import update_last_checked_date
from utils.csv_handler import save_to_csv
from utils.transform_utils import get_response_with_session, get_soup_df, transform_date_to_string

def fetch_data_for_issuer(issuer, last_checked_date):
    new_data = pd.DataFrame(columns=[
        "Date", "Price of last transaction", "Max.", "Min.", "Average price",
        "%prom.", "Quantity", "BEST turnover in denars", "Total turnover in denars"
    ])

    session = requests.Session()
    now = datetime.now()
    years_needed = int((now - last_checked_date).days / 364) + 1

    for i in range(years_needed):
        url = generate_url(issuer, i, last_checked_date)
        soup = get_response_with_session(url, session)
        temp_data = get_soup_df(soup)

        if temp_data is None:
            break

        new_data = pd.concat([new_data, temp_data], ignore_index=True)

    new_data = new_data.drop_duplicates(subset=["Date"])
    new_data["Date"] = pd.to_datetime(new_data["Date"], format="%d.%m.%Y")
    new_data = new_data.sort_values(by="Date")

    new_data["BEST turnover in denars"] = pd.to_numeric(
        new_data["BEST turnover in denars"].apply(lambda x: str(x).replace(',', '')
                                                  .strip() if isinstance(x, str) else x), errors='coerce'
    )
    new_data["Total turnover in denars"] = pd.to_numeric(
        new_data["Total turnover in denars"].apply(lambda x: str(x).replace(',', '')
                                                   .strip() if isinstance(x, str) else x), errors='coerce'
    )

    filtered_data = new_data[
        (new_data["BEST turnover in denars"] != 0) |
        (new_data["Total turnover in denars"] != 0)
        ]

    filtered_data.loc[:, "BEST turnover in denars"] = filtered_data["BEST turnover in denars"].fillna(0)
    filtered_data.loc[:, "Total turnover in denars"] = filtered_data["Total turnover in denars"].fillna(0)

    if not filtered_data.empty:
        save_to_csv(issuer, filtered_data)
    else:
        print(f"No valid data for issuer {issuer} to save.")

    return filtered_data

def fill_missing_data(issuer, last_checked_date):
    print(f"Last checked date for {issuer}: {last_checked_date}")

    now = datetime.now()
    if last_checked_date.date() != now.date():
        print(f"Fetching data from {last_checked_date.strftime('%d.%m.%Y')} to {now.strftime('%d.%m.%Y')}")
        update_missing_data(issuer, last_checked_date)

    update_last_checked_date(now)

def update_missing_data(issuer, last_checked_date):
    new_data = fetch_data_for_issuer(issuer, last_checked_date)
    if new_data is not None:
        process_and_save_data(issuer, new_data)

def process_and_save_data(issuer, data):
    data.columns = [
        "Датум", "Цена на последна трансакција", "Мак.", "Мин.", "Просечна цена",
        "%пром.", "Количина", "Промет во БЕСТ во денари", "Вкупен промет во денари"
    ]

    data['Датум'] = pd.to_datetime(data['Датум'], errors='coerce').dt.strftime('%d.%m.%Y')

    data = filter_data(data)

    save_to_csv(issuer, data)

def filter_data(data):
    data["Промет во БЕСТ во денари"] = pd.to_numeric(data["Промет во БЕСТ во денари"]
                                                     .apply(lambda x: str(x).replace(',', '')
                                                            .strip() if isinstance(x, str) else x), errors='coerce')
    data["Вкупен промет во денари"] = pd.to_numeric(data["Вкупен промет во денари"]
                                                    .apply(lambda x: str(x).replace(',', '')
                                                           .strip() if isinstance(x, str) else x), errors='coerce')

    filtered_data = data[
        (data["Промет во БЕСТ во денари"] != 0) | (data["Вкупен промет во денари"] != 0)
        ]

    return filtered_data

def generate_url(issuer, i, last_date):
    url = f'https://www.mse.mk/mk/stats/symbolhistory/{issuer.lower()}'
    days = 364
    now = datetime.now()
    offset_days = days * i
    to_date = now - timedelta(days=offset_days)
    from_date = to_date - timedelta(days=days)

    if from_date < last_date:
        from_date = last_date + timedelta(days=1)

    return url + (f'?FromDate={transform_date_to_string(from_date)}'
                  f'&ToDate={transform_date_to_string(to_date)}&Code={issuer}')