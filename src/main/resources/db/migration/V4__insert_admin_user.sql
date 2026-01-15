-- Insert or Update the admin user
-- Password hash for '09032005Manh$' is '$2b$10$XoZf3JRtrwEyd4i.pbdIAeeHxQnDR1ZeGmBPtZssCywX095LVh1rO'
INSERT INTO users (
        email,
        password,
        full_name,
        role,
        created_at,
        updated_at
    )
VALUES (
        'manhtranlhp@gmail.com',
        '$2b$10$XoZf3JRtrwEyd4i.pbdIAeeHxQnDR1ZeGmBPtZssCywX095LVh1rO',
        'Manh Tran',
        'ADMIN',
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO
UPDATE
SET role = 'ADMIN',
    password = '$2b$10$XoZf3JRtrwEyd4i.pbdIAeeHxQnDR1ZeGmBPtZssCywX095LVh1rO',
    full_name = 'Manh Tran';