-- Safely update showtimes table schema
DO $$ BEGIN -- 1. Make movie_id nullable (if it exists and is not already nullable)
-- Postgres doesn't have a simple "IF EXISTS" for ALTER COLUMN, but we can just run it.
-- To be safe against "column does not exist", we can check information_schema, but typically movie_id exists.
ALTER TABLE showtimes
ALTER COLUMN movie_id DROP NOT NULL;
EXCEPTION
WHEN OTHERS THEN RAISE NOTICE 'Error altering movie_id, possibly column does not exist or other issue: %',
SQLERRM;
END $$;
DO $$ BEGIN -- 2. Add content_id column if it doesn't exist
IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'showtimes'
        AND column_name = 'content_id'
) THEN
ALTER TABLE showtimes
ADD COLUMN content_id BIGINT;
END IF;
END $$;
DO $$ BEGIN -- 3. Add foreign key constraint for content_id if it doesn't exist
IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'fk_showtimes_content'
) THEN
ALTER TABLE showtimes
ADD CONSTRAINT fk_showtimes_content FOREIGN KEY (content_id) REFERENCES content(id);
END IF;
EXCEPTION
WHEN duplicate_object THEN NULL;
-- Constraint already exists, ignore
END $$;