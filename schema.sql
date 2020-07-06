-- 2020-06-18 Using sqlite, at least for now.

-- For the text describing a language dtext.id = dtext.lang
create table defini (
        id int not null,           -- definition id
        is_language int default 0, -- default to false
        lang int,    -- fk to id of the language of this definition
        phrase text, -- definition text in the language
        myword text -- single word or phrase
        );

create unique index idx1 on defini (id, lang, phrase, myword);


