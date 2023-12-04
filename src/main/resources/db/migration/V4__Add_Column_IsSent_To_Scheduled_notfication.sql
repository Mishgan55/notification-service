alter table notification_db.public.scheduled_notifications
    add column is_sent boolean;
alter table notification_db.public.scheduled_notifications
    add column find_by_topic varchar;