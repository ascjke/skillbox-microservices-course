CREATE INDEX idx_hash_user_male
    ON users_scheme._user USING hash(male);

CREATE INDEX idx_hash_user_deleted
    ON users_scheme._user USING hash(deleted);

CREATE INDEX idx_btree_user_city
    ON users_scheme._user USING btree(city);

CREATE INDEX idx_btree_user_username
    ON users_scheme._user USING btree(username);

CREATE INDEX idx_btree_user_gender_city
    ON users_scheme._user USING btree(male, city);


-- Indexes for FK
CREATE INDEX idx_hash_follower_user_id
    ON users_scheme.subscription USING hash(user_id);

CREATE INDEX idx_hash_following_user_id
    ON users_scheme.following USING hash(user_id);

CREATE INDEX idx_hash_user_hard_skills_user_id
    ON users_scheme.user_hard_skills USING hash(user_id);
