package com.mariobros.objects;


public class CoinBoxCoin extends CoinBox {
    // Variable para contar el número de monedas restantes en el bloque
    private int coinsRemaining;

    public CoinBoxCoin(Stage s) {
        super(s);
        coinsRemaining = 1; // Establecer un valor predeterminado para el número de monedas en el bloque
 
 
 
    }

        @Override
    public void collision(Sprite s) {
        if (s instanceof Player && supportsPlayer) {
            Player p = (Player) s;
            // Colisiones del eje X
            if (getLeft().intersects(p.getRight()) && p.getSpeed().getAccurateX() > 0) {
                p.getSpeed().setX(0);
                p.setLeftWall((int) x);

            } else if (getRight().intersects(p.getLeft()) && p.getSpeed().getAccurateX() < 0) {
                p.getSpeed().setX(0);
                p.setRightWall((int) x + width);

            } else // Colisiones del eje Y
            if (p.getHead().intersects(getFoot()) && p.isRising()) {
                if (!moving) {
                    moving = true;
                   
                    speed.setY(movingSpeed);
                    if (s.getSpeed().getAccurateY() > movingSpeed) {
                        s.getSpeed().setY(movingSpeed);
                    }
                    s.setY(y + height);

                    // Si hay monedas restantes en el bloque, expulsar una moneda
                    if (coinsRemaining > 0) {
                        coinsRemaining--;
                        Coin c = new Coin(stage);
                        c.setX(x);
                        c.setY(y - c.getHeight());
                        ((MainStage) stage).getSoundsLoader().play("coin.wav", false);

                    }
                } else {
                    s.getSpeed().setY(speed.getAccurateY());
                }
            } else if (p.getFoot().intersects(getHead())) {
                if (!moving) {
                    p.setFloor((int) y);
                }
            }
        }
    }

   


}
