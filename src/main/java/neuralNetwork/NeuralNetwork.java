package neuralNetwork;

import botLearning.GenericBotLearning;
import logic.Bot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NeuralNetwork {

    public NeuralNetwork(int[] layers) {
        layersSize = layers;

        inputs = new float[layersSize[0]];

        float startWeight = 5.0f;
        maxWeight = 7.0f;

        Random r = new Random();
        if (layersSize.length < 2) {
            throw new IllegalArgumentException("Wrong layers (layers count < 2)");
        }
        if (inputs.length != layersSize[0]) {
            throw new IllegalArgumentException("Wrong inputs (inputs != input neurons)");
        }
        weights = new float[layersSize.length - 1][][];
        binomials = new float[layersSize.length - 1][];
        values = new float[layersSize.length - 1][];


        for (int i = 0; i < layersSize.length - 1; i++) {
            weights[i] = new float[layersSize[i + 1]][];
            binomials[i] = new float[layersSize[i + 1]];
            values[i] = new float[layersSize[i + 1]];
            for (int j = 0; j < layersSize[i + 1]; j++) {
                weights[i][j] = new float[layersSize[i]];
                binomials[i][j] = startWeight * (2 * r.nextFloat() - 1);
                values[i][j] = 0;
                for (int k = 0; k < layersSize[i]; k++) {
                    weights[i][j][k] = startWeight * (2 * r.nextFloat() - 1);
                }
            }
        }
    }

    private float[] inputs;
    private int[] layersSize;
    private float[][][] weights;
    private float[][] binomials;
    private float[][] values;
    private float maxWeight;
    private List<Integer> firstNotNull;

    private void calculateValues() {
        for (int i = 0; i < layersSize.length - 1; i++) {
            for (int j = 0; j < values[i].length; j++) {
                calcNeuron(i, j);
            }
        }
    }

    private void calcNeuron(int layer, int index) {
        if (layer == 0) {
            float[] w = weights[0][index];
            float value = 0;
            for (Integer i : firstNotNull) {
                value += inputs[i] * w[i];
            }
            values[0][index] = activationFunction(value + binomials[0][index]);
        } else {
            float[] w = weights[layer][index];
            float value = 0;
            for (int i = 0; i < values[layer - 1].length; i++) {
                value += values[layer - 1][i] * w[i];
            }
            values[layer][index] = activationFunction(value + binomials[layer][index]);
        }
    }

    static float activationFunction(float x) {
        return x > 0 ? x : x * 0.01f;
    }

    public float[] getOutputs(float[] inputs, List<Integer> firstNotNull) {
        if (inputs.length != layersSize[0]) {
            throw new IllegalArgumentException();
        }
        this.inputs = inputs;
        this.firstNotNull = firstNotNull;
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        calculateValues();
        return values[layersSize.length - 2];
    }

    public void mutate(float mutationRate) {
        Random r = new Random();
        for (int i = 0; i < layersSize.length - 1; i++) {
            for (int j = 0; j < layersSize[i + 1]; j++) {
                float mutationValue = binomials[i][j] + mutationRate * (2 * r.nextFloat() - 1);
                if (Math.abs(mutationValue) > maxWeight) {
                    mutationValue = maxWeight * (mutationValue > 0 ? 1 : -1);
                }
                binomials[i][j] = mutationValue;
                for (int k = 0; k < layersSize[i]; k++) {
                    mutationValue =  weights[i][j][k] + mutationRate * (2 * r.nextFloat() - 1);
                    if (Math.abs(mutationValue) > maxWeight) {
                        mutationValue = maxWeight * (mutationValue > 0 ? 1 : -1);
                    }
                    weights[i][j][k] = mutationValue;
                }
            }
        }
    }

    public List<Double> getWeights() {
        List<Double> weightsList = new LinkedList<Double>();
        for (int i = 0; i < layersSize.length - 1; i++) {
            for (int j = 0; j < layersSize[i + 1]; j++) {
                for (int k = 0; k < layersSize[i]; k++) {
                    weightsList.add((double) weights[i][j][k]);
                }
            }
        }
        return weightsList;
    }

    public List<Double> getBinomials() {
        List<Double> binomialsList = new LinkedList<Double>();
        for (int i = 0; i < layersSize.length - 1; i++) {
            for (int j = 0; j < layersSize[i + 1]; j++) {
                binomialsList.add((double) binomials[i][j]);
            }
        }
        return binomialsList;
    }

    public boolean loadData(String way) {
        try {
            BufferedReader bufferedReaderW = new BufferedReader(new InputStreamReader(Bot.class.getResourceAsStream("/" + way + "_weights.txt")));
            BufferedReader bufferedReaderB = new BufferedReader(new InputStreamReader(Bot.class.getResourceAsStream("/" + way + "_binomials.txt")));
            for (int i = 0; i < layersSize.length - 1; i++) {
                for (int j = 0; j < layersSize[i + 1]; j++) {
                    binomials[i][j] = new Float(bufferedReaderB.readLine());
                    for (int k = 0; k < layersSize[i]; k++) {
                        weights[i][j][k] = new Float(bufferedReaderW.readLine());
                    }
                }
            }
            bufferedReaderB.close();
            bufferedReaderW.close();
            System.out.println("DataLoaded");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public NeuralNetwork clone() {
        NeuralNetwork nnClone = new NeuralNetwork(layersSize);
        for (int i = 0; i < layersSize.length - 1; i++) {
            for (int j = 0; j < layersSize[i + 1]; j++) {
                nnClone.binomials[i][j] = binomials[i][j];
                for (int k = 0; k < layersSize[i]; k++) {
                    nnClone.weights[i][j][k] = weights[i][j][k];
                }
            }
        }
        return nnClone;
    }
}
