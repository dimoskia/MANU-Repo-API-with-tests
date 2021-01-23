-- Trigger to update authors in record table if someone edits their personal information

CREATE OR REPLACE FUNCTION cascade_member_name_change()
    RETURNS trigger AS
$$
BEGIN
    UPDATE record
    SET authors = (
        select string_agg(concat(a.first_name, ' ', a.last_name), ', ')
        from record_account ra join account a on ra.account_id=a.id
        where ra.record_id = record.id
        group by ra.record_id
    )
    WHERE id IN (SELECT record_id FROM record_account where account_id = OLD.id);

    RETURN NULL;
END;
$$
    LANGUAGE 'plpgsql';

CREATE TRIGGER on_member_name_change
    AFTER UPDATE OF first_name, last_name ON account
    FOR EACH ROW
EXECUTE PROCEDURE cascade_member_name_change();