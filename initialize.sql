-- Create a covering index, which is apparently a bit more efficient than an index.
-- https://www.sqlite.org/queryplanner.html#covidx

insert into defini (is_language) values (1); -- english
insert into defini (is_language) values (1); -- french
insert into defini (is_language) values (1); -- spanish

insert into dtext (did,lang,phrase,myword) values (1,1,'English language', 'English');
insert into dtext (did,lang,phrase,myword) values (1,2,'l''Anglais','Anglais');
insert into dtext (did,lang,phrase,myword) values (1,3,'idioma en Inglés', 'Inglés');

insert into dtext (did,lang,phrase,myword) values (2,1,'French language','French');
insert into dtext (did,lang,phrase,myword) values (2,2,'langue Française', 'Française');
insert into dtext (did,lang,phrase,myword) values (2,3,'idioma francés',"Francés");


insert into dtext (did,lang,phrase,myword) values (3,1,'Spanish language', 'Spanish');
insert into dtext (did,lang,phrase,myword) values (3,2,'l''Espagnol','Espagnol');
insert into dtext (did,lang,phrase,myword) values (3,3,'lengua Española', 'Española');

insert into dtext (did,lang,phrase,myword) values (4,1,
        'A part of an internal combustion engine, usually made as a removable piece, that closes one end of the engine''s cylinders.', 'cylinder head');
insert into dtext (did,lang,phrase,myword) values (4,2,
        'Partie amovible d''un moteur assurant l''étanchéité d''un ou plusieurs de ses cylindres', 'culasse');
insert into dtext (did,lang,phrase,myword) values (4,3,
        'Parte extraíble de un motor que sella uno o más de sus cilindros', 'culata');

insert into dtext (did,lang,phrase,myword) values (5,1,
        'article, refers to an unnamed singular thing or person; someone or something.','a');
insert into dtext (did,lang,phrase,myword) values (5,2,
        'Un homme, une femme. Quelqu''un ou quelque chose.','un');
insert into dtext (did,lang,phrase,myword) values (5,3,
        'Un hombre, una mujer; alguien o algo. Se utiliza ante nombres no conocidos o que aparecen por vez primera en el discurso o texto.','un');

insert into dtext (did,lang,phrase,myword) values (6,1,
         'A human individual.','person');
insert into dtext (did,lang,phrase,myword) values (6,2,
        'Individu de l''espèce humaine.','personne');
insert into dtext (did,lang,phrase,myword) values (6,3,
        'Individuo de la especie humana.', 'persona');


insert into dtext (did,lang,phrase,myword) values (7,1,
         'Automobile. A vehicle, typically used on roads, carrying few people, powered by an internal combustion engine.','car');
insert into dtext (did,lang,phrase,myword) values (7,2,
        'Véhicule automobile. Un véhicule, généralement utilisé sur les routes, transportant peu de personnes, propulsé par un moteur à combustion interne.','automobile');
insert into dtext (did,lang,phrase,myword) values (7,3,
        'Un vehículo, típicamente utilizado en carreteras, que transporta pocas personas, propulsado por un motor de combustión interna.', 'automóvil');


-- insert into dtext (did,lang,phrase,myword) values (4,1,
--         '','')



.headers off
.mode column

select 'Cylinder head in all 3 languages';

.headers on
.mode column

select (select dtext.myword from dtext where did=bb.lang) as lang,myword, phrase from dtext bb where did=4 and lang in (1,2,3);

.headers off
.mode column

select 'All three languages as defined by each other.';

.headers on
.mode column


select defini.id,
    defini.is_language,
    aa.myword as languge_name,
    aa.lang in_lang,
    (select myword from dtext xx where xx.did=aa.lang and xx.lang=aa.lang) as same_lang_name
from defini, dtext aa
where
    defini.is_language=1
    and
    aa.did = defini.id
order by defini.id;

