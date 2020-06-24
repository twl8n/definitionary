-- 2020-06-18 Using sqlite, at least for now.


create table defini (
        id integer primary key,
        is_language int default 0 -- default to false
        );

-- For the text describing a language dtext.id = dtext.lang
create table dtext (
        did int not null, -- fk to defini.id
        lang int, -- fk to defini.id of the language of this definition
        phrase text, -- definition text in the language
        myword text, -- single word or phrase
        foreign key  (did) references defini (id),
        foreign key (lang) references defini (id)
        );

create unique index idx1 on dtext (did, lang, phrase, myword);

-- create table topword (
--         did int, -- fk to definition id
--         lang int, -- fk to lang id
--         foreign key  (did) references defini (id),
--         foreign key (lang) references defini (id)
--         );

