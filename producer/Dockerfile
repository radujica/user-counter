FROM python:3.8.6-slim

# don't buffer output
ENV PYTHONUNBUFFERED 1

ADD producer /app/producer
COPY ./requirements.txt /app/
COPY ./setup.py /app/
COPY ./run.py /app/

WORKDIR /app

RUN pip install --no-cache-dir .

CMD python run.py
