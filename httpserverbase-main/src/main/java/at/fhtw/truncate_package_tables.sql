truncate acquired_cards;
truncate decks;
truncate packages;
delete from "trades";
delete from "MonsterCards";
truncate  "user";
delete from "userCredentials";
update "user" set coins = 20 where coins = 0;
