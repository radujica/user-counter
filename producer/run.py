from producer.event_producer import produce_events


if __name__ == '__main__':
    for event in produce_events():
        print(event)
