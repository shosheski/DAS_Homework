#utils.transform_utils.py
from datetime import datetime
from bs4 import BeautifulSoup
import pandas as pd

def transform_string_to_date(date_str):
    return datetime.strptime(date_str, "%d.%m.%Y")

def transform_date_to_string(date_obj):
    return date_obj.strftime("%d.%m.%Y")

def get_response_with_session(url, session):
    response = session.get(url)
    return BeautifulSoup(response.text, 'html.parser')

def get_soup_df(soup):
    table = soup.select_one('#resultsTable')
    if table is None:
        return None
    rows = table.select('tbody > tr')

    data = {
        "Date": [], "Price of last transaction": [], "Max.": [], "Min.": [],
        "Average price": [], "%prom.": [], "Quantity": [],
        "BEST turnover in denars": [], "Total turnover in denars": []
    }

    for row in rows:
        cells = row.select('td')
        data["Date"].append(cells[0].text)
        data["Price of last transaction"].append(cells[1].text)
        data["Max."].append(cells[2].text)
        data["Min."].append(cells[3].text)
        data["Average price"].append(cells[4].text)
        data["%prom."].append(cells[5].text)
        data["Quantity"].append(cells[6].text)
        data["BEST turnover in denars"].append(cells[7].text)
        data["Total turnover in denars"].append(cells[8].text)

    return pd.DataFrame(data)