# Määrittelydokumentti

## Aihe

Tarkoituksena on toteuttaa javalla ohjelma, joka etsii lyhimmän reitin verkon kahden solmun välillä erilaisia reitinhakualgoritmeja käyttäen. 
Ohjelman avulla voidaan vertailla algoritmien nopeuseroja ja läpikäytyjä solmuja reitinhaussa, sekä mahdollisesti visualisoida niiden reitinhakuprosesseja.
Verkkona toimii ruudukko, jossa yksittäinen ruutu kuvaa yhtä solmua ja jokaisesta solmusta on kaari kaikkiin ympäröiviin solmuihin pohjois-etelä- ja itä-länsiakseleilla (yhdestä ruudusta pääsee siis neljään ympäröivään ruutuun). Kaikki kaaret solmujen välillä ovat kustannukseltaan 1 arvoisia.
Käyttäjä voi vapaasti piirtää ruudukkoon esteitä, jotka algoritmin tulee tarvittaessa kiertää, ja valita halutun lähtöpisteen, sekä maalin. 
Tämän jälkeen valitaan käytettävä reitinhakualgoritmi ja ohjelma piirtää lyhimmän reitin maaliin, sekä värittää ja laskee läpikäydyt ruudut ja ilmoittaa reitin hakemiseen kuluneen ajan. Sitten saman tehtävän voi antaa toiselle algoritmille, jolloin tuloksia pääsee vertailemaan. Ratkaistun tehtävän voi halutessaan myös tallentaa kuvana. Mikäli piirtämisen toteuttaminen ruudukkoineen käy liian hankalaksi, toteutetaan reitinhakualgoritmit vain valmiisiin ascii-merkkisiin [karttoihin](https://www.movingai.com/benchmarks/grids.html) ja visualisointi jätetään visualisoijasta toteuttamatta.

## Algoritmit ja toteutus

Ohjelmaan toteutetaan ainakin _A*_, sekä _Dijkstran algoritmi_, sillä ne ovat tyypillisiä reitinhakualgoritmeja lyhimmän reitin etsimiseen. _A*_ mahdollisesti myös laajennetaan
_jump point search_:llä, jolloin päästään myös vertailemaan tavallista ja optimoitua _A*_:ä. _JPS_:n tulisi ainakin oikeanlaisissa olosuhteissa tehostaa _A*_:n toimintaa<sup>[1]</sup>. Aikavaativuuksissa pyritään algoritmien yleisesti tunnettuihin aikavaativuuksiin, eli _Dijkstran_ osalta O=(n + m log n) (missä n solmujen määrä ja m kaarten määrä) <sup>[2]</sup> ja _A*_ O=(b<sup>d</sup>) (missä b "branching factor" ja d tarkasteltujen solmujen syvyys)<sup>[3]</sup>. Vaikka ohjelmalle piirretään aluksi ruudukosta muodostuva visuaalinen kartta ratkaistavaksi, käännetään ruudukko algoritmille ascii-merkeistä muodostuvaksi taulukoksi. Ascii-merkkien käsittely lienee ohjelmalle (ja toteuttajalle) helpompaa kuin pikselien tarkastelu. Tällöin ohjelmaa voidaan käyttää myös valmiilla ascii-merkeistä tehdyillä kartoilla.

![](https://github.com/chipfrog/Kartan-ratkaisija/blob/master/dokumentaatio/esimerkkikuva.png)
_Esimerkkikuva ohjelmasta_

## Lähteet

1. https://en.wikipedia.org/wiki/Jump_point_search
2. https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
3. https://en.wikipedia.org/wiki/A*_search_algorithm
