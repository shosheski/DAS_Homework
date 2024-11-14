#filters.fetch_issuers.py
import requests
from bs4 import BeautifulSoup
import re

def fetch_issuers():
    issuer_list = []
    url = 'https://www.mse.mk/mk/stats/symbolhistory/kmb'
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    dropdown = soup.select_one('#Code')
    options_list = dropdown.select('option')
    for option in options_list:
        if not re.search(r'\d', option.text):
            issuer_list.append(option.text)

    return issuer_list
