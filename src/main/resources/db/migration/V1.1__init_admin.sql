-- Init Admin Profile Migration Script
INSERT INTO profile (
    id,
    full_name,
    email,
    phone,
    password,
    role,
    region,
    city,
    latitude,
    longitude,
    is_verified,
    is_blocked,
    bonus_balance,
    status,
    avatar_attach_id,
    visible,
    created_date
) VALUES (
             '0b19f213-7830-457f-83df-b970de3a4122', -- Admin UUID
             'System Admin',
             'umareshqurbonov52@gmail.com',
             '+998998887766',
             '$2a$10$R/Igkn93XZq06dLPQ0S69.1V2zrWXUSCzYT38OpFZA1hx7EbyopkW',
             'ROLE_ADMIN', -- yoki ADMIN (ProfileRoleEnum ga qarab)
             'Tashkent',
             'Yashnabod',
             41.2995,
             69.2401,
             TRUE,
             FALSE,
             0.00,
             'ACTIVE', -- GeneralStatusEnum
             NULL,
             TRUE,
             CURRENT_TIMESTAMP
         ) ON CONFLICT (email) DO NOTHING;