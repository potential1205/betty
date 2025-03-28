CREATE TABLE teams (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       team_code VARCHAR(2) NOT NULL UNIQUE,
                       team_name VARCHAR(50) NOT NULL,
                       token_code VARCHAR(3) NOT NULL
);


INSERT INTO teams (team_code, team_name, token_code)
VALUES
    ('LT', '롯데 자이언츠', 'LGT'),
    ('WO', '키움 히어로즈', 'KWN'),
    ('NC', 'NC 다이노스', 'NCN'),
    ('LG', 'LG 트윈스', 'LGT'),
    ('OB', '두산 베어스', 'OBB'),
    ('KT', 'KT 위즈', 'KTT'),
    ('SK', 'SSG 랜더스', 'SSG'),
    ('HT', 'KIA 타이거즈', 'KIA'),
    ('SS', '삼성 라이온즈', 'SSL'),
    ('HH', '한화 이글스', 'HHE');
