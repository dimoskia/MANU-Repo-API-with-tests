CREATE MATERIALIZED VIEW recent_record AS
SELECT id, title, collection, date_archived, authors
FROM record
WHERE approved = TRUE AND private_record = FALSE
ORDER BY date_archived DESC
LIMIT 5;

CREATE MATERIALIZED VIEW popular_record AS
SELECT id, title, collection, downloads_count, authors
FROM record
WHERE approved = TRUE AND private_record = FALSE
ORDER BY downloads_count DESC
LIMIT 5;

CREATE MATERIALIZED VIEW records_per_collection AS
SELECT collection, count(*) as record_count
FROM record
WHERE approved = TRUE AND private_record = FALSE
GROUP BY collection;

CREATE MATERIALIZED VIEW records_per_department AS
SELECT department, count(*) as record_count
FROM record
WHERE approved = TRUE AND private_record = FALSE
GROUP BY department;

create unique index on recent_record(id);
create unique index on popular_record(id);
create unique index on records_per_collection (collection);
create unique index on records_per_department (department);
