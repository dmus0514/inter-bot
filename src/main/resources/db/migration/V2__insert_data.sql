insert into levels(name, description) values ('Junior', 'Junior Java Developers are entry-level roles that offer an opportunity to learn and grow in software development, focusing on Java programming and software design principles');
insert into levels(name, description) values ('Middle', 'A middle Java developer as often referred to, typically has 2-5 years of experience and is proficient in writing, testing, debugging, and maintaining Java code');
insert into levels(name, description) values ('Senior', 'A Senior Java Developer is a highly skilled and experienced professional who leads the design, development, and maintenance of Java-based applications');

insert into topics(topic) values ('Объектно-Ориентированное Программирование');
insert into topics(topic) values ('Класс Object и его методы');
insert into topics(topic) values ('Исключения');
insert into topics(topic) values ('Коллекции, особенно HashMap');
insert into topics(topic) values ('Многопоточность');

insert into questions(topic_id, level_id, question) values (1, 1, 'Что такое объектно-ориентированное программирование и его основные принципы?');
insert into questions(topic_id, level_id, question) values (1, 1, 'В чем заключается разница между наследованием и композицией?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Как реализуется полиморфизм в Java и какие его преимущества?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Когда следует использовать абстрактный класс, а когда интерфейс?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Какие методы класса Object являются ключевыми для реализации ООП?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Что такое переопределение метода и чем оно отличается от перегрузки?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Как реализовать паттерн Singleton и какие могут быть его недостатки?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Как принцип инкапсуляции помогает защитить данные внутри класса?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Как абстракция позволяет скрыть сложность реализации?');
insert into questions(topic_id, level_id, question) values (1, 1, 'Какие принципы SOLID вы знаете и как они применяются в Java?');

insert into questions(topic_id, level_id, question) values (2, 1, 'В чем заключается контракт между методами equals() и hashCode()?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Почему важно переопределять метод toString() в классе Object?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Как работает метод clone() и какие проблемы могут возникнуть при его использовании?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Что делает метод finalize() и почему его использование считается нежелательным?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Чем отличается сравнение объектов с помощью оператора == и метода equals()?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Почему все классы в Java наследуют методы от класса Object?');
insert into questions(topic_id, level_id, question) values (2, 1, 'В каких случаях имеет смысл переопределять метод clone()?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Как проверить, принадлежит ли объект определенному классу, используя возможности класса Object?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Какие особенности следует учитывать при переопределении методов Object в контексте потокобезопасности?');
insert into questions(topic_id, level_id, question) values (2, 1, 'Как методы Object могут быть полезны при отладке и логировании?');
