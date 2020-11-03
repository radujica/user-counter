FROM python:3.8.6-alpine

# don't buffer output
ENV PYTHONBUFFERED 1

ADD producer /app/producer
COPY ./requirements.txt /app/
COPY ./setup.py /app/
COPY ./run.py /app/

WORKDIR /app

RUN apk add --no-cache --virtual .build-deps gcc musl-dev \
    && apk add --no-cache librdkafka-dev \
    && pip install --no-cache-dir . \
    && apk del .build-deps

CMD python run.py
