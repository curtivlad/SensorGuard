-- Initialize SensorGuard Database

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tables will be created automatically by Hibernate
-- This file can contain seed data or custom configurations

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE sensorguard TO sensorguard_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sensorguard_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sensorguard_user;

-- Create indexes for better performance
-- These will be created after tables exist