FROM python:3.8.6-alpine

ADD producer /app/producer
COPY ./requirements.txt /app/
COPY ./setup.py /app/
COPY ./run.py /app/

WORKDIR /app
RUN apk add --no-cache --virtual .build-deps gcc musl-dev
RUN apk add --no-cache librdkafka-dev
RUN pip install --no-cache-dir .
RUN apk del .build-deps

CMD python run.py
