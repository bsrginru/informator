# informator

## Mod к игре Minecraft - Informator

![Logo](https://github.com/bsrginru/informator/blob/master/src/main/resources/avttrue.informator.logo.png)

На данный момент выводится следующая информация:
* Баффы и дебаффы.
* Износ оружия или инструмента в руке, износ одетой брони.
* Информация о блоке, на который направлен взгляд: координаты, освещённость, заряженность и пр.
* Реальное (не игровое) время.
* Игровое время.
* Текущая фаза луны и "фактор фазы".
* Текущая погода (ясно, дождь, гроза).
* Скорость перемещения игрока.
* Список всех зачарований на игроке.
* Информация о мобе, на которого направлен взгляд (для коня и питомцев дополнительно (если есть): владелец, прыгучесть, быстрота).
* Информация о полученном опыте.

![Screenshot](https://github.com/bsrginru/informator/blob/v1.10.x/screenshots/2016-05-02_19.11.54.png)

## Информация об освещённости

Отображается информация об освещённости поверхности (по нажатию на кнопку F12)

![Screenshot](https://github.com/bsrginru/informator/blob/v1.10.x/screenshots/2016-05-02_19.13.01.png)

## Установка и запуск
1. скачиваем [инсталлятор Forge](https://files.minecraftforge.net/ "например forge-1.14.4-28.1.87-installer.jar")
2. выбираем папку, где расположены клиентские файлы (в Windows см. каталог ...\AppData\Roaming\.minecraft) и нажимаем кнопку 'Install Client'
3. в этой же папке создаём каталог mods, копируем в него файл informator.jar
4. запускаем Minecraft Launcher и выбираем в настройках только что установленную версию Forge ( например, forge-1.14.4-28.1.87 )
5. нажимаем кнопку 'ИГРАТЬ'

## Настройка

Настройка:
Основное меню Майнкрафта (там, где кнопки "Одиночная игра" и "Сетевая игра") -> кнопка "Mods" ("Модификации") -> выбираем мод Информатор -> кнопка Config

# Благодарности

Самая большая благодарность авторам этого мода *Chapaev* и *rumickon*, которые сделали первый и наиболее существенный вклад в его разработку!

Отдельная благодарность сообществу minecrafting.ru за помощь в развитии мода!

А также его самым активным тестерам: *evogar*, *Heidenlarm*, *MyIceWind*.

Вопросы и предложения [здесь](http://minecrafting.ru/topic/12250/ "Форум minecrafting.ru").

# Сборка исходных кодов

Для сборки и запуска собранного mod-а потребуется более 2Гб свободного места на диске!

## Сборка мода для minecraft-1.14.4 и forge-28.1.87
1. Создаём каталог для файлов проекта, например ./informator/, переходим в созданный каталог и в командной консоли выполняем:

    git clone git@github.com:bsrginru/informator.git .

    (в созданном каталоге ./informator/ должны появиться файлы проекта, напр. README.md, а также каталог ./informator/src/main/)
2. Cкачиваем [Forge MDK](https://files.minecraftforge.net/ "например, forge-1.14.4-28.1.87-mdk.zip") и распаковываем в папку ./informator/:
  * каталог mdk/gradle/
  * и файл mdk/gradlew.bat для Windows, либо файл mdk/gradlew для Linux
  * уже существующие файлы ./informator/.gitignore и ./informator/build.gradle следует оставить без изменений!
 однако, в случае сборки с версией Forge, отличной от приведённой в примере forge-1.14.4-28.1.87, потребуется сравнить и отредактировать файлы:
    * ./informator/build.gradle
    * ./informator/src/main/resources/pack.mcmeta
    * ./informator/src/main/resources/META-INF/mods.toml
3. В Windows скачиваем [JDK и JRE] (https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html "например, jdk-8u212-windows-x64.exe, jre-8u212-windows-x64.exe" ) устанавливаем. В Linux в конмандной консоли выполняем:

    sudo apt install openjdk-8-jre openjdk-8-jdk

    sudo update-alternatives --config java # выбираем версию java (forge не работает с 11-jre, устанавливаемой по умолчанию)
4. Выполняем в командной консоли:

    ./gradlew --info eclipse

    ./gradlew --info build # из консоли не работает, пока проект не импортирован в eclipse (см. ниже) 

    Указанных команд достаточно для того, чтобы собрать .jar файл и установить его в качается mod-а.

## Настройка среды разработки

Для запуска собираемого мода из среды разработки потребуется более 3Гб памяти.

1. Скачиваем [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/packages/ "например eclipse-java-2019-06-R-win32-x86_64.zip") распаковываем (в скачанном пакете должна присутствовать 'Gradle integration').
2. *Примечание: выполнить 'Getting Started with Forge - [From Zero to Modding](https://mcforge.readthedocs.io/en/latest/gettingstarted/ "From Zero to Modding")' и собрать проект с декомпиляцией исходников minecraft из Eclipse не удалось, официальная инструкция явно устарела.*
Ниже приведена рабочая последовательность действий по декомпиляции исходных кодов minecraft-1.14.4 и forge-1.14.4-28.1.87, можно также [добавить ключи](https://www.minecraftforge.net/forum/topic/16872-setupdecompworkspace-or-setupdevworkspace-where-is-the-difference/) --debug и --refresh-dependencies).
3. Выполняем в командной консоли:

    ./gradlew eclipse
4. Запускаем Eclipse
  * импортируем Gradle-проект
  * во вкладке Package Explorer находим папку src\main, выделяем в ней подкаталоги java и resources и в меню 'Build Path' выбираем пункт 'Use as Source Folder'
  * во вкладке 'Package Explorer' открываем 'Properties for informator', изменяем 'Text file encoding' на 'Other UTF-8', нажимаем 'Apply and Close'
  * во вкладке 'Package Explorer' находим папку src\test и удаляем её
  * для того, чтобы продолжить разработку, потребуется декомпиляция исходных кодов minecraft, в которые постоянно приходится заглядывать и подсматривать что именно и как изменилось (единственный возможный путь в отсутствие документации).

    Выполняем в командной консоли:

    ./gradlew --info --refresh-dependencies jar reobfJar

# Запуск Minecraft вместе с Mod-ом
1. скачиваем [инсталлятор Forge](https://files.minecraftforge.net/ "например forge-1.14.4-28.1.87-installer.jar")
2. выбираем папку, где расположены клиентские файлы и нажимаем кнопку 'Install Client'
3. в этой же папке создаём каталог mods, копируем в него файл ./informator/build/libs/informator.jar
4. запускаем Minecraft Launcher и выбираем в настройках только что установленную версию Forge ( например, forge-1.14.4-28.1.87 )
5. нажимаем кнопку 'ИГРАТЬ'
