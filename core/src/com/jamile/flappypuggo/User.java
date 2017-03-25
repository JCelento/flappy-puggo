package com.jamile.flappypuggo;

public class User {
    private String nome;
    private int score;

    public User(String nome, int score) {
        this.nome = nome;
        this.score = score;
    }

    public String getNome() {

        return nome;
    }

    public void setNome(String nome) {

        this.nome = nome;
    }


    public void setScore(int score) {

        this.score = score;
    }

    public int getScore() {

        return score;
    }
}
