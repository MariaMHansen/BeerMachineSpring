package net.codejava;

import org.springframework.stereotype.Component;

public class Machine {
    //attr
    int test=15;
    int curentState;
    int productsProduced;
    private static Machine instance = new Machine();
        // constructor for singleton
        private Machine(){} // private constructor
        // getter
        public static Machine getInstance(){
            return instance;
        }

    //setters and getters

    public int getTest() { return test;}
    public void setTest(int test) {this.test = test;}

    public int getCurentState() {
        return curentState;
    }
    public void setCurentState(int curentState) {
        this.curentState = curentState;
    }
    public int getProductsProduced() {
        return productsProduced;
    }
    public void setProductsProduced(int productsProduced) {
        this.productsProduced = productsProduced;
    }
    // to String

    @Override
    public String toString() {
        return "Machine{" +
                "test=" + test +
                ", curentState=" + curentState +
                ", productsProduced=" + productsProduced +
                '}';
    }
}
