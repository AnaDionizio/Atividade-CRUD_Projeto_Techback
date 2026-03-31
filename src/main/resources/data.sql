-- Inserção de dados iniciais para Funcionarios
INSERT INTO funcionarios (nome, cargo) VALUES ('João Silva', 'Desenvolvedor');
INSERT INTO funcionarios (nome, cargo) VALUES ('Maria Santos', 'Analista');
INSERT INTO funcionarios (nome, cargo) VALUES ('Carlos Oliveira', 'Gerente');
INSERT INTO funcionarios (nome, cargo) VALUES ('Ana Pereira', 'Designer');
INSERT INTO funcionarios (nome, cargo) VALUES ('Pedro Lima', 'Tester');

-- Inserção de dados iniciais para Filmes
INSERT INTO filmes (titulo, sinopse, data_lancamento, genero, duracao_minutos, classificacao_indicativa) VALUES ('O Senhor dos Anéis: A Sociedade do Anel', 'Uma aventura épica na Terra Média', '2001-12-19', 'Fantasia', 178, '12 anos');
INSERT INTO filmes (titulo, sinopse, data_lancamento, genero, duracao_minutos, classificacao_indicativa) VALUES ('O Senhor das Moscas', 'Clássico da literatura adaptado para o cinema', '1963-08-13', 'Drama', 92, '14 anos');
INSERT INTO filmes (titulo, sinopse, data_lancamento, genero, duracao_minutos, classificacao_indicativa) VALUES ('Matrix', 'Um hacker descobre a verdade sobre sua realidade', '1999-03-31', 'Ficção Científica', 136, '16 anos');
INSERT INTO filmes (titulo, sinopse, data_lancamento, genero, duracao_minutos, classificacao_indicativa) VALUES ('Titanic', 'Uma história de amor a bordo do navio Titanic', '1997-12-19', 'Romance', 195, '12 anos');
INSERT INTO filmes (titulo, sinopse, data_lancamento, genero, duracao_minutos, classificacao_indicativa) VALUES ('Inception', 'Um ladrão que rouba segredos corporativos através do uso de tecnologia de compartilhamento de sonhos', '2010-07-16', 'Ficção Científica', 148, '14 anos');

-- Inserção de dados iniciais para Usuarios
INSERT INTO usuarios (username, password, email) VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@example.com');
INSERT INTO usuario_roles (usuario_id, role) VALUES (1, 'ADMIN');
