ALTER TABLE monitor_app
    DROP INDEX uk_bundle_platform_region;

ALTER TABLE monitor_app
    ADD UNIQUE KEY uk_app_link_platform (app_link, store_platform);
