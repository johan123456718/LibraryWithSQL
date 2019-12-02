use laboration1;

CREATE TABLE T_Book(
isbn BIGINT PRIMARY KEY,
title VARCHAR(255) NOT NULL,
genre ENUM('fantasy', 'sci-fi', 'crime', 'drama', 'romance', 'science'),
rating int,
publisher VARCHAR(255) NOT NULL,
datePublished DATE NOT NULL,
CONSTRAINT		T_Book_isbn_cc check(length(isbn) = 13)
);

CREATE TABLE T_Author(
authorId INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(30) NOT NULL,
dob DATE NOT NULL
);

CREATE TABLE WrittenBy(
authorID	int,
isbn		bigint,
CONSTRAINT		T_WrittenBy_isbn_fk FOREIGN KEY (isbn) REFERENCES T_Book(isbn),
CONSTRAINT		T_WrittenBy_authorID_fk FOREIGN KEY (authorID) REFERENCES  T_Author(authorID),
PRIMARY KEY(authorID, isbn)
);

