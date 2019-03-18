package Model;

import Commit.EmptyEvm;
import Commit.IEvm;
import Commit.IRobot;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Tri Bằng - VUWIT14
 */
public class Robot implements IRobot{
    private IEvm iEvm;
    private static Robot unique;
    private Environment environment;
    private int i, j;
    public int radius, count;

    private Robot() {
    }

    public static Robot getIntance() {
        if (unique == null) {
            unique = new Robot();
        }
        return unique;
    }

    public void update(Environment newEnvironment, int valueI, int valueJ) {
        environment = newEnvironment;
        i = valueI;
        j = valueJ;
    }

    public void start() {
        //--Comit giao dien neu co the
        if(iEvm == null){
            iEvm = new EmptyEvm();
        }
        //---
        System.out.println("Starting position of the robot: " + i + ", " + j);
        count = environment.getCountDust(); // đếm số rác trong môi trường
        System.out.println("Number of garbage: " + environment.getCountDust());

        // thực hiện kiểm tra vị trí mà robot được thả xem có rác không
        if (environment.getMatrix()[i][j] == 1) {
            count--;
            suck();
            System.out.println("Remaining number of garbage: " + count);
        }

        // Process the robot around and go to the garbage
        Point point = null;
        while (count != 0) {
            point = searchDust(); //Find the difference between the garbage and the standing position (this is the shortest distance between the garbage and the robot
            go(point.x, point.y); // The robot starts from the standing position to the rubbish position
            count--;
            suck(); // The robot makes garbage and the environment from garbage state (1) into clean state (0)
            System.out.println("Remaining number of garbage: " + count);
        }
        commitDone();
    }

    // phương thức tìm khoảng cách từ robot đến các vị trí rác xung quanh nó
    private Point searchDust() {
        ArrayList<Point> list = new ArrayList<Point>(); // mảng chứa danh sách khoảng cách từ robot đến rác
        radius = 0; // bán kính quét của robot

        // Perform a sweep around: the radius grows from 1, 2, 3 ... until the garbage stops
        while (true) {
            radius++;
            commitRadar();
            for (int d = 0; d <= radius; d++) {
                if (condition(i + d, j - radius) && environment.getMatrix()[i + d][j - radius] == 1) {
                    list.add(new Point(d, -radius));
                }
                if (condition(i - d, j + radius) && environment.getMatrix()[i - d][j + radius] == 1) {
                    list.add(new Point(-d, radius));
                }
                if (condition(i - radius, j - d) && environment.getMatrix()[i - radius][j - d] == 1) {
                    list.add(new Point(-radius, -d));
                }
                if (condition(i + radius, j + d) && environment.getMatrix()[i + radius][j + d] == 1) {
                    list.add(new Point(radius, d));
                }
                if (d != 0 || d != radius) {
                    if (condition(i - d, j - radius) && environment.getMatrix()[i - d][j - radius] == 1) {
                        list.add(new Point(-d, -radius));
                    }
                    if (condition(i + d, j + radius) && environment.getMatrix()[i + d][j + radius] == 1) {
                        list.add(new Point(d, radius));
                    }
                    if (condition(i - radius, j + d) && environment.getMatrix()[i - radius][j + d] == 1) {
                        list.add(new Point(-radius, d));
                    }
                    if (condition(i + radius, j - d) && environment.getMatrix()[i + radius][j - d] == 1) {
                        list.add(new Point(radius, -d));
                    }
                }
                // When you see the robot litter will return the distance between the robot and the garbage
                if (!list.isEmpty()) {
                    Random random = new Random();
                    return list.get(random.nextInt(list.size()));
                }
            }
        }
    }

//The mode for the robot goes from the standing position to the garbage position
    private void go(int row, int column) {
        if (row != 0 && column != 0) {
            goRow(row);
            goColumn(column);
            return;
        }

        if (row == 0) {
            goColumn(column);
            return;
        }

        if (column == 0) {
            goRow(row);
            return;
        }
    }

    // phương thức cho robot đi theo cột
    private void goColumn(int column) {// trục dy
        if (column > 0) {
            while (column-- != 0) {
                right();
            }
        } else {
            while (column++ != 0) {
                left();
            }
        }
    }

    // phương thức cho robot đi theo hàng
    private void goRow(int row) {// trục dx
        if (row > 0) {
            while (row-- != 0) {
                down();
            }
        } else {
            if (row < 0) {
                while (row++ != 0) {
                    up();
                }
            }
        }
    }

    // kiểm tra vị trí có tồn tại trong ma trận không
    private boolean condition(int tempX, int tempY){
        if(0 <= tempX && tempX < environment.getRow() &&
                0 <= tempY && tempY < environment.getColumn()){
            return true;
        }else{
            return false;
        }
    }

    private void up() {
        i = i - 1;
        System.out.println("Move up to: " + i + ", "+ j);
        commitMoveUp();
        return;
    }

    private void down() {
        i = i + 1;
        System.out.println("Scroll down to: " + i + ", "+ j);
        commitMoveDown();
        return;
    }

    private void left() {
        j = j - 1;
        System.out.println("Move left to: " + i + ", "+ j);
        commitMoveLeft();
        return;
    }

    private void right() {
        j = j + 1;
        System.out.println("Move right through to: " + i + ", "+ j);
        commitMoveRight();
        return;
    }

    private void suck() {
        environment.getMatrix()[i][j] = 0;
        System.out.println("Scan at position: " + i + ", " + j);
        System.out.println("Matrix status:");
        environment.printMatrix();
        commitSuck();
        return;
    }

    public void registry(IEvm iEvm) {
        this.iEvm = iEvm;
    }

    public void commitMoveUp() {
        iEvm.updateMoveUp();
    }

    public void commitMoveDown() {
        iEvm.updateMoveDown();
    }

    public void commitMoveLeft() {
       iEvm.updateMoveLeft();
    }

    public void commitMoveRight() {
       iEvm.updateMoveRight();
    }

    public void commitDone() {
         iEvm.updateDone();
    }

    public void commitSuck() {
        iEvm.updateSuck();
    }

    public void commitRadar() {
        iEvm.updateRadar();
    }

    public boolean isReady(){
        if(environment == null)
            return false;
        return true;
    }

    
}
