CREATE TABLE IF NOT EXISTS USER_PREFERENCES
(
    VANISH_USER UUID NOT NULL PRIMARY KEY,
    ITEM_SETTING BOOLEAN NOT NULL,
    INTERACT_SETTING BOOLEAN NOT NULL,
    NOTIFY_SETTING BOOLEAN NOT NULL,
    LOCATION_SETTING BOOLEAN NOT NULL,
    NIGHTVISION_SETTING BOOLEAN NOT NULL
)