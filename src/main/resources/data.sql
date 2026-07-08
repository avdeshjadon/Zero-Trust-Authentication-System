-- Insert Default Roles
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_USER');

-- Insert Default Permissions
INSERT IGNORE INTO permissions (id, name) VALUES (1, 'READ_ALL_USERS'), (2, 'MANAGE_USERS'), (3, 'READ_PROFILE'), (4, 'UPDATE_PROFILE');

-- Assign Permissions to Roles (Admin)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (1, 1), (1, 2), (1, 3), (1, 4);

-- Assign Permissions to Roles (User)
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (2, 3), (2, 4);
