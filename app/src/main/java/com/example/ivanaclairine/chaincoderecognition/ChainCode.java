package com.example.ivanaclairine.chaincoderecognition;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivana Clairine on 9/29/2016.
 */
public class ChainCode {

    public static final int black = -16711423;
    public static final int white = -1;
    public List<Integer> directionX;
    public List<Integer> directionY;
    public List<List<Integer>> numberPattern;

    Bitmap bitmap;
    int[][] pixels;
    int width;
    int height;

    public ChainCode(Bitmap bm){
        bitmap = toBlackAndWhite(bm);
        pixels = new int[bm.getWidth()][bm.getHeight()];
        width = bm.getWidth();
        height = bm.getHeight();
        directionX = new ArrayList<>();
        directionY = new ArrayList<>();
        numberPattern = new ArrayList<>();

        for(int x=0; x<width; x++){
            for(int y=0; y<height; y++){
                pixels[x][y] = bitmap.getPixel(x,y);
            }
        }
        assignDirectionRule();
        assignNumberPattern();
    }

    public void assignDirectionRule(){
        directionX.add(0, 0); directionY.add(0, -1);
        directionX.add(1, 1); directionY.add(1, -1);
        directionX.add(2, 1); directionY.add(2, 0);
        directionX.add(3, 1); directionY.add(3, 1);
        directionX.add(4, 0); directionY.add(4, 1);
        directionX.add(5, -1); directionY.add(5, 1);
        directionX.add(6, -1); directionY.add(6, 0);
        directionX.add(7, -1); directionY.add(7, -1);
    }

    public void assignNumberPattern(){
        List<Integer> numb0 = new ArrayList<>();
        numb0.add(2); numb0.add(4);numb0.add(6);numb0.add(0);
        numberPattern.add(0, numb0);
        numberPattern.add(1, numb0);

        List<Integer> numb2 = new ArrayList<>();
        numb2.add(2); numb2.add(4); numb2.add(6); numb2.add(4); numb2.add(2); numb2.add(4);
        numb2.add(6); numb2.add(0); numb2.add(2); numb2.add(0); numb2.add(6); numb2.add(0);
        numberPattern.add(2, numb2);

        List<Integer> numb3 = new ArrayList<>();
        numb3.add(2); numb3.add(4); numb3.add(6); numb3.add(0); numb3.add(2); numb3.add(0);
        numb3.add(6); numb3.add(0); numb3.add(2); numb3.add(0); numb3.add(6); numb3.add(0);
        numberPattern.add(3, numb3);

        List<Integer> numb4 = new ArrayList<>();
        numb4.add(2); numb4.add(4); numb4.add(2); numb4.add(0); numb4.add(2);
        numb4.add(4); numb4.add(6); numb4.add(0); numb4.add(6); numb4.add(0);
        numberPattern.add(4, numb4);

        List<Integer> numb5 = new ArrayList<>();
        numb5.add(2); numb5.add(4); numb5.add(6); numb5.add(4); numb5.add(2); numb5.add(4);
        numb5.add(6); numb5.add(0); numb5.add(2); numb5.add(0); numb5.add(6); numb5.add(0);
        numberPattern.add(5, numb5);

        List<Integer> numb6 = new ArrayList<>();
        numb6.add(2); numb6.add(4); numb6.add(6); numb6.add(4); numb6.add(2); numb6.add(4);
        numb6.add(6); numb6.add(0);
        numberPattern.add(6, numb6);

        List<Integer> numb7 = new ArrayList<>();
        numb7.add(2); numb7.add(4);numb7.add(6);numb7.add(0); numb7.add(6); numb7.add(0);
        numberPattern.add(7, numb7);

        numberPattern.add(8, numb0);

        List<Integer> numb9 = new ArrayList<>();
        numb9.add(2); numb9.add(4); numb9.add(6); numb9.add(0);
        numb9.add(2); numb9.add(0); numb9.add(6); numb9.add(0);
        numberPattern.add(9, numb9);
    }

    public List<Integer> doChainCodeSmooth(){
        return removeDuplicateList(removeNoise(doChainCode2px()));
    }

    public List<Integer> doChainCode2px(){
        List<Integer> chainCode = new ArrayList<>();
        int[] pointStart = startPixel();

        int xStart = pointStart[0];
        int yStart = pointStart[1];
        int xNow = xStart;
        int yNow = yStart;
        int direction = 2;

        do{
            direction = cariArah(xNow, yNow, direction);
            xNow = xNow + directionX.get(direction);
            yNow = yNow + directionY.get(direction);
            chainCode.add(direction);
        }while(xNow != xStart || yNow != yStart);

        return chainCode;
    }

    public int cariArah(int x, int y, int direction){
        //cek arah sebelumnya, jika ada maka pindahkan titik sekarang ke arah itu

        int arah;

        arah = (direction + 7)%8;

        boolean stop = false;

        while(!stop){
            if(isBlackOnDirection(x, y, arah)){
                stop = true;
            }
            else{
                arah = (arah+1)%8;
            }
        }

        return arah;
    }

    public int doRecognize (List<Integer> chainCode){
        boolean stop = false;
        int i=0;

        while(!stop) {
            List<Integer> temp = numberPattern.get(i);
            if(isMatch(temp, chainCode))
                return i;
            else
                i++;
        }
        return 0;
    }

    public boolean isMatch(List<Integer> input1, List<Integer> input2){
        boolean stop = false;
        int i=0;

        while(!stop && i<input1.size()){
            if(input1.size() != input2.size())
                return false;
            else{
                if(input1.get(i) != input2.get(i))
                    return false;
            }
            i++;
        }

        return true;
    }

    public boolean isBlackOnDirection(int x, int y, int direction){
        if(pixels[x+directionX.get(direction)][y+directionY.get(direction)] == black)
            return true;
        return false;
    }

    public int[] startPixel(){
        int[] retval = new int[2];
        retval[0] = -1;
        retval[1] = -1;
        boolean stop = false;

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if(pixels[x][y] == black){
                    retval[0] = x;
                    retval[1] = y;
                    stop = true;
                    break;
                }
            }
            if(stop) break;
        }

        return retval;
    }

    public Bitmap toBlackAndWhite(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        int[] pix = new int[width * height];
        bmpOriginal.getPixels(pix, 0, width, 0, 0, width, height);

        int R, G, B,Y;

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++)
            {
                int index = y * width + x;
                int Red = (pix[index] >> 16) & 0xff;     //bitwise shifting
                int Green = (pix[index] >> 8) & 0xff;
                int Blue = pix[index] & 0xff;
//                Log.d("Pixel: ", String.valueOf(pix[index]));
//                Log.d("Red: " , String.valueOf(Red));
//                Log.d("Green: " , String.valueOf(Green));
//                Log.d("Blue: " , String.valueOf(Blue));

                int average = (Red + Green + Blue) / 3;
                int color = 255;
                if(average < 128)
                {
//                    Log.d("Warna: ", "hitam");
                    color = 1;
                }
                //R,G.B - Red, Green, Blue
                //to restore the values after RGB modification, use
                //next statement
                pix[index] = 0xff000000 | (color << 16) | (color << 8) | color;
            }}

        Bitmap bmpGrayscale = Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
        return bmpGrayscale;
    }

    public int countBlackNeighbor(int x, int y){
        int counter = 0;

        //cek P2
        if(pixels[x][y-1] == black){
            counter++;
        }

        //cek P3
        if(pixels[x+1][y-1] == black){
            counter++;
        }

        //cek P4
        if(pixels[x+1][y] == black){
            counter++;
        }

        //cek P5
        if(pixels[x+1][y+1] == black){
            counter++;
        }

        //cek P6
        if(pixels[x][y+1] == black){
            counter++;
        }

        //cek P7
        if(pixels[x-1][y+1] == black){
            counter++;
        }

        //cek P8
        if(pixels[x-1][y] == black){
            counter++;
        }

        //cek P9
        if(pixels[x-1][y-1] == black){
            counter++;
        }

        return counter;
    }

    public List<Integer> getNeighbor (int x, int y){
        List<Integer> neighbors = new ArrayList<>();

        //P2
        neighbors.add(0, pixels[x][y]);
        neighbors.add(1, pixels[x][y]);

        neighbors.add(2, pixels[x][y-1]);
        neighbors.add(3, pixels[x+1][y-1]);
        neighbors.add(4, pixels[x+1][y]);
        neighbors.add(5, pixels[x+1][y+1]);
        neighbors.add(6, pixels[x][y+1]);
        neighbors.add(7, pixels[x-1][y+1]);
        neighbors.add(8, pixels[x-1][y]);
        neighbors.add(9, pixels[x-1][y-1]);
        neighbors.add(10,pixels[x][y-1]);

        return neighbors;
    }

    public int[] convert2Dto1D (int[][] input){
        int[] retArray = new int[width*height];
        int indeks = 0;

        for(int i=0; i<height; i++){
            for(int j=0; j< width; j++){
                retArray[indeks] = pixels[j][i];
                indeks++;
            }
        }
        return retArray;
    }

    public List<Integer> removeDuplicateList (List<Integer> input){
        List<Integer> retList = new ArrayList<>();
        retList.add(input.get(0));

        for(Integer i: input){
            if(i != retList.get(retList.size()-1)){
                retList.add(i);
            }
        }
        return retList;
    }

    public List<Integer> removeNoise (List<Integer> input){
        List<Integer> retList = new ArrayList<>();
        int temp = input.get(0);
        int i=0;
        int counter = 1;

        while(i < input.size()-1){
            if(input.get(i+1) == temp){
                counter++;
            }
            else{
                if(counter > 5){
                    retList.add(temp);
                }
                temp = input.get(i);
                counter = 1;
            }
            i++;
        }

        //keluar dari while
        if(counter > 5)
            retList.add(temp);

        return retList;
    }
}
