package io.cdap.wrangler.plugin;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.TokenType;

import java.util.*;
import java.util.stream.Collectors;

public class AggregateDirective implements Directive {
    private String groupByColumn;
    private String aggregateColumn;
    private String operation;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate");
        builder.define("groupByColumn", TokenType.COLUMN_NAME);
        builder.define("aggregateColumn", TokenType.COLUMN_NAME);
        builder.define("operation", TokenType.TEXT);
        return builder.build();
    }


    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.groupByColumn = args.value("groupByColumn");
        this.aggregateColumn = args.value("aggregateColumn");
        this.operation = args.value("operation").toString().toLowerCase(); // Use getValue() to extract the string
        Set<String> validOps = new HashSet<>(Arrays.asList("sum", "avg", "min", "max"));
        if (!validOps.contains(this.operation)) {
            throw new DirectiveParseException("Invalid aggregation operation: " + operation);
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        try {
            Map<Object, List<Row>> grouped = rows.stream()
                    .collect(Collectors.groupingBy(row -> row.getValue(groupByColumn)));

            List<Row> result = new ArrayList<>();
            for (Map.Entry<Object, List<Row>> entry : grouped.entrySet()) {
                Object groupKey = entry.getKey();
                List<Row> groupRows = entry.getValue();

                List<Double> values = new ArrayList<>();
                for (Row row : groupRows) {
                    Object val = row.getValue(aggregateColumn);
                    if (val != null) {
                        values.add(toDoubleOrThrow(val));
                    }
                }


                Double aggregatedValue;
                switch (operation) {
                    case "sum":
                        aggregatedValue = values.stream().mapToDouble(Double::doubleValue).sum();
                        break;
                    case "avg":
                        aggregatedValue = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                        break;
                    case "min":
                        aggregatedValue = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                        break;
                    case "max":
                        aggregatedValue = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                        break;
                    default:
                        throw new DirectiveExecutionException("Invalid aggregation operation");
                }

                Row aggregatedRow = new Row();
                aggregatedRow.add(groupByColumn, groupKey);
                aggregatedRow.add(aggregateColumn + "_" + operation, aggregatedValue);
                result.add(aggregatedRow);
            }

            return result;
        } catch (DirectiveExecutionException e) {
            throw e; // Re-throw the exception if necessary
        } catch (Exception e) {
            throw new DirectiveExecutionException("Error during execution: " + e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        // no-op
    }

    private double toDoubleOrThrow(Object value) throws DirectiveExecutionException {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new DirectiveExecutionException("Non-numeric value in aggregation column: " + value);
    }
}
