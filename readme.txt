для сборки и запуска собранного mod-а потребуется более 2Гб свободного места на диске

скачиваем forge mdk ( forge-1.14.3-27.0.43-mdk.zip ) распаковываем в папку informator
скачиваем jdk и jre ( jdk-8u212-windows-x64.exe, jre-8u212-windows-x64.exe ) устанавливаем
скачиваем eclipse ( eclipse-java-2019-06-R-win32-x86_64.zip ) распаковываем

(выполнить 'Getting Started From Zero' Gradle Tasks из Eclipse не удалось, как и воспользоваться инструкцией https://mcforge.readthedocs.io/en/latest/gettingstarted/ )

(возможно не хватало указания ключа --refresh-dependencies как указано здесь: https://www.minecraftforge.net/forum/topic/16872-setupdecompworkspace-or-setupdevworkspace-where-is-the-difference/ )

в папке informator из консоли выполняем:
 gradlew setupDecompWorkspace --refresh-dependencies --debug﻿ о блоке, на который направлен взгляд: координаты, освещённость, заряженность и пр.
Реальное (не игровое) время.
 gradlew setupDevWorkspace --refresh-dependencies --de﻿bug﻿﻿﻿﻿
 gradlew eclipse --debug﻿﻿﻿

переходим в папку informator, удаляем всё содержимое папки src\main и из консоли выполняем в этой папке:
 git clone git@github.com:bsrginru/informator.git .

после чего уже запускаем Eclipse
во вкладке Package Explorer находим папку src\main, выделяем в ней подкаталоги java и resources и в меню 'Build Path' выбираем пункт 'Use as Source Folder'

скачиваем инсталлятор Forge ( forge-1.14.3-27.0.43-installer.jar )
выбираем папку, где расположены клиентские файлы и нажимаем кнопку 'Install Client'
в этой же папке создаём каталог mods, копируем в него build\libs\informator.jar