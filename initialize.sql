-- Create a covering index, which is apparently a bit more efficient than an index.
-- https://www.sqlite.org/queryplanner.html#covidx

-- insert into defini (is_language) values (1); -- english
-- insert into defini (is_language) values (1); -- french
-- insert into defini (is_language) values (1); -- spanish

insert into defini (is_language, id, lang,phrase,myword) values (1,1,1,'English language', 'English');
insert into defini (is_language, id, lang,phrase,myword) values (1,1,2,'l''Anglais','Anglais');
insert into defini (is_language, id, lang,phrase,myword) values (1,1,3,'idioma en Inglés', 'Inglés');

insert into defini (is_language, id, lang,phrase,myword) values (1,2,1,'French language','French');
insert into defini (is_language, id, lang,phrase,myword) values (1,2,2,'langue Française', 'Française');
insert into defini (is_language, id, lang,phrase,myword) values (1,2,3,'idioma francés',"Francés");

insert into defini (is_language, id, lang,phrase,myword) values (1,3,1,'Spanish language', 'Spanish');
insert into defini (is_language, id, lang,phrase,myword) values (1,3,2,'l''Espagnol','Espagnol');
insert into defini (is_language, id, lang,phrase,myword) values (1,3,3,'lengua Española', 'Española');

insert into defini (id, lang,phrase,myword) values (4,1,
        'A part of an internal combustion engine, usually made as a removable piece, that closes one end of the engine''s cylinders.', 'cylinder head');
insert into defini (id, lang,phrase,myword) values (4,2,
        'Partie amovible d''un moteur assurant l''étanchéité d''un ou plusieurs de ses cylindres', 'culasse');
insert into defini (id, lang,phrase,myword) values (4,3,
        'Parte extraíble de un motor que sella uno o más de sus cilindros', 'culata');

insert into defini (id, lang,phrase,myword) values (5,1,
        'article, refers to an unnamed singular thing or person; someone or something.','a');
insert into defini (id, lang,phrase,myword) values (5,2,
        'Un homme, une femme. Quelqu''un ou quelque chose.','un');
insert into defini (id, lang,phrase,myword) values (5,3,
        'Un hombre, una mujer; alguien o algo. Se utiliza ante nombres no conocidos o que aparecen por vez primera en el discurso o texto.','un');

insert into defini (id, lang,phrase,myword) values (6,1,
         'A human individual.','person');
insert into defini (id, lang,phrase,myword) values (6,2,
        'Individu de l''espèce humaine.','personne');
insert into defini (id, lang,phrase,myword) values (6,3,
        'Individuo de la especie humana.', 'persona');

insert into defini (id, lang,phrase,myword) values (7,1,
         'Automobile. A vehicle, typically used on roads, carrying few people, powered by an internal combustion engine.','car');
insert into defini (id, lang,phrase,myword) values (7,2,
        'Véhicule automobile. Un véhicule, généralement utilisé sur les routes, transportant peu de personnes, propulsé par un moteur à combustion interne.','automobile');
insert into defini (id, lang,phrase,myword) values (7,3,
        'Un vehículo, típicamente utilizado en carreteras, que transporta pocas personas, propulsado por un motor de combustión interna.', 'automóvil');


-- insert into defini (id, lang,phrase,myword) values (4,1,
--         '','')



.headers off
.mode column

select 'Cylinder head in all 3 languages';

.headers on
.mode column

select (select myword from defini where id=outx.lang) as lang,myword, phrase from defini outx where id=4 and lang in (1,2,3);

.headers off
.mode column

select 'All three languages as defined by each other.';

.headers on
.mode column

select id,
    (select myword from defini where id=aa.id and lang=id) lang_name,
    ' in ' "in",
    (select myword from defini where id=aa.lang and lang=aa.id) as target_lang,
    ' is ' "is",
    myword as translated_name,
    lang in_lang
from defini aa
where
    is_language=1
order by id;

