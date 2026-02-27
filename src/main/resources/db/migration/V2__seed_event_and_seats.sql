insert into events (id, name, starts_at)
values ('event_123', 'Demo Event', now() + interval '7 days')
    on conflict (id) do nothing;

insert into seats (event_id, seat_no)
values
    ('event_123', 'A1'),
    ('event_123', 'A2'),
    ('event_123', 'A3'),
    ('event_123', 'A4'),
    ('event_123', 'A5')
    on conflict (event_id, seat_no) do nothing;