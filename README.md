# mobilalk_merfoldko2

Kis segítség a javításhoz.

1. Fordítási hiba - Nincs
2. Futtatási hiba - Nincs
3. Firebase Autentikáció - Regisztráció (RegistationActivity 79-138 sor), Bejelentkezés (MainActivity 75-101 sor) 
4. Adatmodell definiálás - 2 osztály is van (UserData és ReservationData)
5. 4 activity - 7 activity van, ebből 4 (MainActivity, RegistationActivity, HomePageActivity, TableChoosingActivity)
6. Beviteli mezők beviteli típusa megfelelő - Minden helyen megfelelő pl (MainActivity.xml 44. sor vagy 57. sor)
7. ConstraintLayout és még egy másik layout típus használata - ConstraintLayout-ot, LinearLayout-ot, FrameLayout-ot is használok
8. Reszponzív - Szerintem minden lapon tökéletesen elrendeztem mindent, ahol nem tudtam ott Scrollview-al oldottam meg
9. Legalább 2 animáció - Mindkettő a főoldalon van (animation_one és -_two, a HomePageActivity 106-112. sorában vannak implementálva)
10. Intentek használata - minden oldalt el lehet érni, egyszerű a navigáció
11. 2 rendszerszerszolgáltatás - Van Notification és alarm manager
12. CRUD műveletek - a profilban lehet változtatni a felhasználóneve, lehet törölni a fiókot, időpontot lehet foglalni és látod is a foglalásokat a foglalásaim oldalon
13. 3 Firestore lekérdezés - 1: ReservationDoneActivity 58. sortól, 2: 
