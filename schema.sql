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
        foreign key  (did) references defini (id),
        foreign key (lang) references defini (id)
        );

create table topword (
        did int, -- fk to definition id
        lang int, -- fk to lang id
        myword text, -- single word or phrase
        foreign key  (did) references defini (id),
        foreign key (lang) references defini (id)
        );

-- Create a covering index, which is apparently a bit more efficient than an index.
-- https://www.sqlite.org/queryplanner.html#covidx

create unique index idx1 on topword (did, lang, myword);

insert into defini (is_language) values (1);
insert into dtext (did,lang,phrase) values ((select id from defini where rowid=last_insert_rowid()),(select id from defini where rowid=last_insert_rowid()),'English language');
insert into topword (did,lang,myword) values ((select id from defini where rowid=last_insert_rowid()),(select id from defini where rowid=last_insert_rowid()),'English');

insert into defini (is_language) values (1);
insert into dtext (did,lang,phrase) values ((select id from defini where rowid=last_insert_rowid()),(select id from defini where rowid=last_insert_rowid()),'langue Française');
insert into topword (did,lang,myword) values ((select id from defini where rowid=last_insert_rowid()),(select id from defini where rowid=last_insert_rowid()),'Française');

insert into defini (is_language) values (1);
insert into dtext (did,lang,phrase) values ((select id from defini where rowid=last_insert_rowid()),(select id from defini where rowid=last_insert_rowid()),'lengua Española');
insert into topword (did,lang,myword) values ((select id from defini where rowid=last_insert_rowid()),(select id from defini where rowid=last_insert_rowid()),'Española');

insert into dtext (did,lang,phrase) values   (2,1,'French language');
insert into topword (did,lang,myword) values (2,1,'French');
insert into dtext (did,lang,phrase) values   (3,1,'Spanish language');
insert into topword (did,lang,myword) values (3,1,'Spanish');


insert into dtext (did,lang,phrase) values   (1,2,"l'Anglais");
insert into topword (did,lang,myword) values (1,2,'Anglais');
insert into dtext (did,lang,phrase) values   (3,2,"l'Espagnol");
insert into topword (did,lang,myword) values  (3,2,"Espagnol");

insert into dtext (did,lang,phrase) values   (1,3,"idioma en Inglés");
insert into topword (did,lang,myword) values (1,3,'Inglés');
insert into dtext (did,lang,phrase) values   (2,3,"idioma francés");
insert into topword (did,lang,myword) values  (2,3,"Francés");


.headers on
.mode column
select defini.id,defini.is_language,zz.myword,dtext.lang,(select myword from topword xx where xx.did=dtext.lang) as lang
from defini, dtext, topword zz
where
    defini.is_language=1
    and
    dtext.did = defini.id
    and
    defini.id = zz.did
    and
    dtext.lang = zz.lang
order by defini.id;

select defini.id from defini, dtext, topword
where
    defini.is_language=1
    and
    defini.id=dtext.lang
    and
    defini.id = topword.did
    and
    topword.myword='English';
