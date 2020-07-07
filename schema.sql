-- 2020-06-18 Using sqlite, at least for now.

-- For the text describing a language dtext.id = dtext.lang
create table defini (
        id int not null,           -- definition id
        lang int,                  -- self-ref to id of the language of this definition
        is_language int default 0, -- default to false
        myword text,   -- single word or phrase
        phrase text    -- definition text in the language
        );

-- 2020-07-06 Are both of these necessary?
create unique index idx1 on defini (id, lang);
create unique index idx2 on defini (id, lang, phrase, myword);


