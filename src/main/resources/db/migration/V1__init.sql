create table if not exists events (
                                      id text primary key,
                                      name text not null,
                                      starts_at timestamptz not null,
                                      created_at timestamptz not null default now()
    );

create table if not exists seats (
                                     id bigserial primary key,
                                     event_id text not null references events(id) on delete cascade,
    seat_no text not null,
    created_at timestamptz not null default now(),
    unique (event_id, seat_no)
    );

create table if not exists reservations (
                                            id bigserial primary key,
                                            event_id text not null references events(id) on delete cascade,
    seat_id bigint not null references seats(id) on delete cascade,
    status text not null,
    hold_token text null,
    expires_at timestamptz null,
    booked_at timestamptz null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint reservations_status_chk check (status in ('HELD', 'BOOKED')),
    constraint reservations_hold_fields_chk check (
(status = 'HELD' and hold_token is not null and expires_at is not null and booked_at is null)
    or
(status = 'BOOKED' and hold_token is null and expires_at is null and booked_at is not null)
    )
    );

create unique index if not exists ux_reservations_booked_seat
    on reservations(event_id, seat_id)
    where status = 'BOOKED';

create unique index if not exists ux_reservations_active_hold_seat
    on reservations(event_id, seat_id)
    where status = 'HELD';