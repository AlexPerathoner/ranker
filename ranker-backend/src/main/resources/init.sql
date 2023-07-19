// postgresql database

drop table edges;
drop table users_series;
drop table users;
drop table series;

create table if not exists series (
    id bigint primary key,
    meta varchar(600) not null
);

create table if not exists users (
    id varchar(50) primary key
);

create table if not exists users_series (
    user_id varchar(50),
    media_id bigint not null,
    pagerank_value numeric(12, 11),
    constraint pk_users_series primary key (user_id, media_id),
    constraint fk_user_id foreign key (user_id) references users(id),
    constraint fk_media_id foreign key (media_id) references series(id)
);

create table if not exists edges (
    better_id bigint not null,
    worse_id bigint not null,
    username varchar(50) not null,
    constraint fk_better_id foreign key (better_id) references series(id),
    constraint fk_worse_id foreign key (worse_id) references series(id),
    constraint pk_edges primary key (better_id, worse_id, username),
    constraint fk_username foreign key (username) references users(id)
);


insert into series (id, meta) values (99147, '{"title": {"romaji": "Shingeki no Kyojin 3", "english": "Attack on Titan Season 3", "userPreferred": "Shingeki no Kyojin 3"}, "coverImage": {"extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx99147-5RXELRvwjFl6.jpg", "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx99147-5RXELRvwjFl6.jpg", "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx99147-5RXELRvwjFl6.jpg", "color": "#4386e4"}}');
insert into series (id, meta) values (102351, '{"title": {"romaji": "Tokyo Ghoul:re 2","english": "Tokyo Ghoul:re 2","userPreferred": "Tokyo Ghoul:re 2"},"coverImage": {"extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx102351-yD3Ty9YZFMsf.jpg","large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx102351-yD3Ty9YZFMsf.jpg","medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx102351-yD3Ty9YZFMsf.jpg","color": "#f1e4c9"}}');
insert into series (id, meta) values (16498, '{"title": {"romaji": "Shingeki no Kyojin","english": "Attack on Titan","userPreferred": "Shingeki no Kyojin"},"coverImage": {"extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx16498-C6FPmWm59CyP.jpg","large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx16498-C6FPmWm59CyP.jpg","medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx16498-C6FPmWm59CyP.jpg","color": "#e4a15d"}}');
insert into users (id) values ('Piede');
insert into users_series (user_id, media_id) values ('Piede', 99147);
insert into users_series (user_id, media_id) values ('Piede', 102351);
insert into users_series (user_id, media_id) values ('Piede', 16498);

select * from users_series, series where users_series.media_id = series.id and users_series.user_id = 'Piede';