from utils import write, write_non_extractable, write_processing
from urllib import request, error
from bs4 import BeautifulSoup
from bs4.element import Comment
from nlp import *
from html.parser import HTMLParser
import re


def tag_visible(element):
    if element.parent.name in ['style', 'script', 'head', 'title', 'meta', '[document]']:
        return False
    if isinstance(element, Comment):
        return False
    return True


def soup_text(soup):
    # TODO: revised soup scheme
    # not sure if it's working well
    for script in soup(["script", "style"]):  # remove all javascript and stylesheet code
        script.extract()
    # get text
    text = soup.get_text()
    # break into lines and remove leading and trailing space on each
    lines = (line.strip() for line in text.splitlines())
    # break multi-headlines into a line each
    chunks = (phrase.strip() for line in lines for phrase in line.split("  "))
    # drop blank lines
    text = '\n'.join(chunk for chunk in chunks if chunk)
    return text

# def soup_text(soup):
#     texts = soup.findAll(text=True)
#     visible_texts = filter(tag_visible, texts)
#     text = u' '.join(t.strip() for t in visible_texts).encode('utf-8')
#     # get rid of any whitespace (newlines, spaces, tabs, etc.) between two newlines
#     # text = re.sub(r'\n\s*\n', r'\n\n', text.strip(), flags=re.M).encode('utf-8')
#     # text = re.sub(r'\n\s*\n', r'\n\n', text.strip(), flags=re.M)
#     return text


def retrieve_and_extract(url, relation_flag, confidence_threshold):
    write_processing(url)
    html = request.urlopen(url).read()
    soup = BeautifulSoup(html, 'html.parser')
    text = soup_text(soup)
    if not text:
        write_non_extractable()
        return
    # t = "-LSB- 2 -RSB- -LSB- 3 -RSB- In 1975 , Gates and Allen launched Microsoft , which became the world 's largest PC software company ."
    set_text(text)
    return nlp(relation_flag, confidence_threshold)




