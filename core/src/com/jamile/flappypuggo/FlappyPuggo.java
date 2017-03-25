package com.jamile.flappypuggo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Random;

public class FlappyPuggo extends ApplicationAdapter implements ApplicationListener {
    private SpriteBatch batch;
    private Texture[] puggo;
    private Texture fundo; // Background
    private Texture canoBaixo; // Bottom burguer tower
    private Texture canoTopo; // Top burguer tower
    private Texture gameOver;
    private Random numeroRandomico; // Random numer
    private BitmapFont fonte; // Font
    private BitmapFont mensagem; // Messages
    private BitmapFont mensagem2;
    private BitmapFont nome; // User name
    private BitmapFont pontuacoes; // Scores


    private User usuario;

    private Json json;

    private Sound sound;
    private Music music;

    private String tituloDialogo; // Dialog Box Tittle
    private String messageUser;
    private String nomeUser;
    private String score;
    private String scores;


    private boolean afficher=false;// Determina se houve postagem da caixa de input - Determines if the input box was posted or not
    private boolean fim=false; // Marca o fim da rodada - Indicates the end


    private Circle puggoCirculo; // Forma criada para gerar a colisão - Shape created to generate the collision
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    // private ShapeRenderer shape; Criado para visualizar o teste das colisões - Created to test the collisions

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768; // Medidas virtuais para garantir fit em qualquer tela - To ensure a good fit with any screen i'm using virtual measures
    private final float VIRTUAL_HEIGHT = 1024;


    private float larguraCel; // <-  Vou guardar aqui as medidas reais da tela do celular  -  And here is the real measures from the cell phone (width)
    private float alturaCel; // <- (height)
    private float posicaoInicialVert; // Initial vertical position
    private float velocidadeQueda; // Floating speed
    private float posicaoMovimentoCanoHorizontal; // Horizontal position of the burgers
    private float espacoEntreCanos;// Space size between the burguer
    private float deltaTime;

    private float alturaEntreCanosRandomica; // Random size between the burguer
    private int estadoJogo=0; // 0 = Jogo não iniciado - 0 = Game not started
    private int pontuacao=0; // Score
    private int pontuacaoTest=0; // Score test, to increase difficult when user reach certain score
    private int gibeDificuldade=0; // Difficult level
    private int cont=0; // Counter
    private float variacao=0; // Variation
    private boolean ponto = false; // Score bug correction




    @Override
	public void create () {

        tituloDialogo = "Digite seu nome:"; // Dialog box asking for user name
        afficher = false; // Display not visible

        sound = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
        batch = new SpriteBatch();
        numeroRandomico = new Random(); // Random number

        puggoCirculo = new Circle(); // Circle around the puggo
        retanguloCanoBaixo = new Rectangle(); // Rectangle for bottom buger tower
        retanguloCanoTopo = new Rectangle(); // Rectangle for the top
        // shape = new ShapeRenderer();

        fonte = new BitmapFont();
        fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fonte.getData().setScale(4);

        mensagem = new BitmapFont();
        mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        mensagem.getData().setScale(3);

        nome = new BitmapFont();
        nome.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        nome.getData().setScale(2);

        mensagem2 = new BitmapFont();
        mensagem2.setColor(Color.CORAL);
        mensagem2.getData().setScale(2);

        pontuacoes  = new BitmapFont();
        pontuacoes.setColor(Color.GREEN);
        pontuacoes.getData().setScale(3);

        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String nome) {

                                    // Touch the screen to start + name
                    messageUser = "Toque na tela para começar, " + nome + " ";

                nomeUser=nome;

                afficher=true;     // Display visible

            }

            @Override
            public void canceled() {
                messageUser = "Não posso registrar seu nome :("; // Cannot register your name :(
                nomeUser="Sem nome";   // Without name
                afficher=true;
            }
        }, tituloDialogo, "", "Nome"); // Dialog box tittle

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setVolume(0.1f);                 // sets the volume to half the maximum volume
        music.setLooping(true);                // will repeat playback until music.stop() is called
        music.play();                          // inicia a musica - starts the song


        puggo = new Texture[7]; // Puggo animation using a array of textures
        puggo[0] = new Texture("puggo1.png");
        puggo[1] = new Texture("puggo2.png");
        puggo[2] = new Texture("puggo3.png");
        puggo[3] = new Texture("puggo4.png");
        puggo[4] = new Texture("puggo5.png");
        puggo[5] = new Texture("puggo6.png");
        puggo[6] = new Texture("puggo7.png");



        canoBaixo = new Texture("cano_baixo.png"); // Bottom burguers texture
        canoTopo = new Texture("cano_topo.png"); // Top burguers texture
        fundo = new Texture("fundo.png"); // Background Texture
        gameOver = new Texture("game_over.png");

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);


        larguraCel = VIRTUAL_WIDTH;
        alturaCel = VIRTUAL_HEIGHT;

        posicaoInicialVert = alturaCel/2; // Initial vertical position = cellphone height / 2
        posicaoMovimentoCanoHorizontal = larguraCel; // Initial position of the burguers = cellphone width / 2
        espacoEntreCanos = 300; // Initial space between burguers towers

	}

	@Override
	public void render () {

        camera.update();

        // Limpar os frames - Clear the frames

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10;

        if (variacao > 6)
            variacao = 0;

        if(estadoJogo==0){
            music.pause();                         // Pauses the playback
            if(Gdx.input.justTouched()){
                estadoJogo=1;
                music.play();
            }

        }
        else {

            if(pontuacaoTest==5){ // After every 5 points the difficult level is increased
               gibeDificuldade= gibeDificuldade+120;
                pontuacaoTest=0;
            }

            velocidadeQueda--; // Floating velocity is dropping
            if (posicaoInicialVert > 0 || velocidadeQueda < 0)
                posicaoInicialVert = posicaoInicialVert - velocidadeQueda;

            if (estadoJogo == 1) {
                posicaoMovimentoCanoHorizontal -= deltaTime * (200+gibeDificuldade);



                if (Gdx.input.justTouched()) {
                    sound.play(0.2f);
                    velocidadeQueda = +15;
                }

                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraCel;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    ponto=false;
                }

                if (posicaoMovimentoCanoHorizontal<120) {
                    if (!ponto) {
                        pontuacao++;
                        ponto = true;
                        pontuacaoTest++;

                    }
                }
            }
        else // Game over
            {
                // Restart if touched
                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    ponto = false;
                    velocidadeQueda = 0;
                    posicaoInicialVert = alturaCel/2;
                    posicaoMovimentoCanoHorizontal = larguraCel;
                    gibeDificuldade=0;
                    afficher=false;
                    fim=false;

                }
            }

        }

        batch.setProjectionMatrix(camera.combined);

        batch.begin();



        batch.draw(fundo,0,0,larguraCel,alturaCel);  // Background
        batch.draw(canoTopo,posicaoMovimentoCanoHorizontal,alturaCel/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica);     // Top Burguer Tower
        batch.draw(canoBaixo,posicaoMovimentoCanoHorizontal,alturaCel/2 - canoBaixo.getHeight()- espacoEntreCanos/2 + alturaEntreCanosRandomica);    // Bottom Burguer Tower
        if (estadoJogo==1)
            batch.draw(puggo[(int)variacao],120,posicaoInicialVert);
        else
            batch.draw(puggo[1],120,posicaoInicialVert);
        fonte.draw(batch,String.valueOf(pontuacao),larguraCel/2,alturaCel-50);
        if(afficher && estadoJogo==0){
            nome.draw(batch, messageUser, 10, alturaCel-600);

        }
        if(estadoJogo==2) { // Scores board
            FileHandle file = Gdx.files.local("scores.json");

            // Writing in a json file

                scores = file.readString();

                scores = scores.replace("{", "");
                scores = scores.replace("}", "");
                scores = scores.replace("nome:", "Nome:");
                scores = scores.replace("score:", "Pontuação:");

            // Writing from the file to the screen

            pontuacoes.draw(batch, "v Pontuações v", larguraCel / 2 - gameOver.getWidth() / 2 +30, alturaCel - 220 - 2 * gameOver.getHeight());
            nome.draw(batch, scores, larguraCel / 2 - gameOver.getWidth() / 2 + gameOver.getWidth() / 4, alturaCel - 120 - 4 * gameOver.getHeight());


            batch.draw(gameOver, larguraCel / 2 - gameOver.getWidth() / 2, alturaCel -100);
            mensagem2.draw(batch,"Puggo não pode comer essas coisas :(", larguraCel / 2 - gameOver.getWidth()/8 - gameOver.getWidth()/2, alturaCel -100 - gameOver.getHeight()/3);
                                // Puggo cannot eat these foods :(
            mensagem.draw(batch,"Toque para reiniciar", larguraCel / 2 - gameOver.getWidth()/2, alturaCel -100 - gameOver.getHeight());
        }                       // Touch to restart

        batch.end();

        puggoCirculo.set(120 + puggo[0].getWidth()/2 , posicaoInicialVert + puggo[0].getHeight()/2 , puggo[0].getWidth()/2);

        retanguloCanoBaixo.set(posicaoMovimentoCanoHorizontal, alturaCel/2 - canoBaixo.getHeight()- espacoEntreCanos/2 + alturaEntreCanosRandomica, canoBaixo.getWidth(), canoBaixo.getHeight());

        retanguloCanoTopo.set(posicaoMovimentoCanoHorizontal, alturaCel/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica, canoTopo.getWidth(), canoTopo.getHeight());

        /* Desenho das formas para teste - Drawing shapes for testing
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(puggoCirculo.x, puggoCirculo.y, puggoCirculo.radius);
        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);

        shape.setColor(com.badlogic.gdx.graphics.Color.RED);
        shape.end();*/

        // Teste de colisão - Collision Testing
        if(Intersector.overlaps(puggoCirculo, retanguloCanoBaixo)|| Intersector.overlaps(puggoCirculo, retanguloCanoTopo) || posicaoInicialVert<=0 || posicaoInicialVert>=alturaCel){
            estadoJogo=2;

        }


	if(estadoJogo==2 && fim==false){
        usuario= new User(nomeUser,pontuacao);
        json = new Json();
        score = json.prettyPrint(usuario);
        FileHandle file = Gdx.files.local("scores.json");
        if (cont<4){
        file.writeString(score, true);   // True = append
        cont++;}
       else {
            file.writeString(score, false);         // False = overwrite
            cont=0;
        }
        fim=true;

    }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
