package utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("rawtypes") // We ignore this warning since we want raw types and cannot infer types
public class CustomAssert {

    public static void assertObjectsEqual(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null) {
            fail("Expected element was null but actual element was not null.");
        }
        if (actual == null) {
            fail("Expected element was not null but actual element was null.");
        }

        if (expected.getClass() != actual.getClass()) {
            fail("Expected element to be of type '" + expected.getClass().getName() + "' but was of type '"
                    + actual.getClass().getName() + "'.");
        }

        final var messageBuilder = new StringBuilder();
        // Only fail if the message is not empty
        if (generateErrorsFromComparison(expected, actual, messageBuilder, "")) {
            var message = messageBuilder.toString();
            message = message.substring(0, message.length() - System.getProperty("line.separator").length());
            fail(message);
        }
    }

    private static boolean generateErrorsFromComparison(Object expected, Object actual, StringBuilder messageBuilder, String currentScope) {
        var foundError = false;
        if(findErrorsNullChecks(expected, actual, messageBuilder, currentScope)){
            return true;
        }
        if(findErrorsCollections(expected, actual, messageBuilder, currentScope)){
            foundError = true;
        }
        final var fields = expected.getClass().getDeclaredFields();
        for (var field : fields) {
            try {
                field.setAccessible(true);
                final var expectedPropertyValue = field.get(expected);
                final var actualPropertyValue = field.get(actual);

                // Skip null properties
                if(expectedPropertyValue == null && actualPropertyValue == null) {
                    continue;
                }

                if(findErrorsNullChecks(expectedPropertyValue, actualPropertyValue, messageBuilder, currentScope + "." + field.getName())){
                    foundError = true;
                    continue;
                }

                if(isPrimitiveOrPrimitiveWrapper(field)){
                    if(findErrorsValueComparison(expectedPropertyValue, actualPropertyValue, messageBuilder, currentScope + "." + field.getName())){
                        foundError = true;
                    }
                }else{
                    if(generateErrorsFromComparison(expectedPropertyValue, actualPropertyValue, messageBuilder, currentScope + "." + field.getName())) {
                        foundError = true;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return foundError;
    }

    private static boolean findErrorsNullChecks(Object expectedPropertyValue, Object actualPropertyValue, StringBuilder messageBuilder, String currentScope) {
        if (expectedPropertyValue == null && actualPropertyValue != null) {
            messageBuilder.append("Expected '").append(currentScope)
                    .append("' to be null but was not null.").append(System.getProperty("line.separator"));
            return true;
        }
        if (expectedPropertyValue != null && actualPropertyValue == null) {
            messageBuilder.append("Expected '").append(currentScope)
                    .append("' to be not null but was null.").append(System.getProperty("line.separator"));
            return true;
        }
        return false;
    }

    private static boolean findErrorsCollections(Object expectedPropertyValue, Object actualPropertyValue, StringBuilder messageBuilder, String currentScope) {
        var foundError = false;
        if(expectedPropertyValue == null || actualPropertyValue == null) {
            return false;
        }
        if(expectedPropertyValue.getClass() != actualPropertyValue.getClass()) {
            return false;
        }
        if(expectedPropertyValue.getClass().isArray()) {
            final var expectedLength = Array.getLength(expectedPropertyValue);
            final var actualLength = Array.getLength(actualPropertyValue);
            final var isPrimitiveArray = isPrimitiveOrPrimitiveWrapper(expectedPropertyValue.getClass().getComponentType());
            if(expectedLength != actualLength) {
                messageBuilder.append("Expected '")
                    .append(currentScope)
                    .append("' to be of length ")
                    .append(expectedLength)
                    .append(" but was of length ")
                    .append(actualLength)
                    .append(".")
                    .append(System.getProperty("line.separator"));
                return true;
            }
            for(int i = 0; i < expectedLength; i++) {
                final var expectedPropertyValueAtIndex = Array.get(expectedPropertyValue, i);
                final var actualPropertyValueAtIndex = Array.get(actualPropertyValue, i);
                if(isPrimitiveArray) {
                    if(findErrorsValueComparison(expectedPropertyValueAtIndex, actualPropertyValueAtIndex, messageBuilder, currentScope + "[" + i + "]")){
                        foundError = true;
                    }
                }else{
                    if(generateErrorsFromComparison(expectedPropertyValueAtIndex, actualPropertyValueAtIndex, messageBuilder, currentScope + "[" + i + "]")) {
                        foundError = true;
                    }
                }
            }
        }
        if(expectedPropertyValue instanceof final List expectedPropertyValueAsList) {
            final var actualPropertyValueAsList = (List) actualPropertyValue;
            if(expectedPropertyValueAsList.size() != actualPropertyValueAsList.size()) {
                messageBuilder.append("Expected '")
                    .append(currentScope)
                    .append("' to be of size ")
                    .append(expectedPropertyValueAsList.size())
                    .append(" but was of size ")
                    .append(actualPropertyValueAsList.size())
                    .append(".")
                    .append(System.getProperty("line.separator"));
                return true;
            }
            // Compare index by index
            if(expectedPropertyValueAsList.size() > 0) {
                final var isPrimitiveList = isPrimitiveOrPrimitiveWrapper(expectedPropertyValueAsList.get(0).getClass());

                for(int i=0;i< expectedPropertyValueAsList.size();i++) {
                    final var expectedPropertyValueAtIndex = expectedPropertyValueAsList.toArray()[i];
                    final var actualPropertyValueAtIndex = actualPropertyValueAsList.toArray()[i];
                    if(isPrimitiveList) {
                        if(findErrorsValueComparison(expectedPropertyValueAtIndex, actualPropertyValueAtIndex, messageBuilder, currentScope + "[" + i + "]")){
                            foundError = true;
                        }
                    }else{
                        if(generateErrorsFromComparison(expectedPropertyValueAtIndex, actualPropertyValueAtIndex, messageBuilder, currentScope + "[" + i + "]")) {
                            foundError = true;
                        }
                    }
                }
            }
        }else if(expectedPropertyValue instanceof final Iterable expectedPropertyValueAsIterable) {
            final var actualPropertyValueAsIterable = (Iterable) actualPropertyValue;

            var firstSize = 0;
            var secondSize = 0;
            for (final Object ignored : expectedPropertyValueAsIterable) {
                firstSize++;
            }
            for (final Object ignored : actualPropertyValueAsIterable) {
                secondSize++;
            }

            if(firstSize != secondSize) {
                messageBuilder.append("Expected '")
                .append(currentScope)
                .append("' to be of size ")
                .append(firstSize)
                .append(" but was of size ")
                .append(secondSize)
                .append(".")
                .append(System.getProperty("line.separator"));
                return true;
            }
            
            final var symmetricDifference = findSymmetricDifference(expectedPropertyValueAsIterable, actualPropertyValueAsIterable);
            if(symmetricDifference.size() > 0) {
                messageBuilder.append("Expected '")
                    .append(currentScope)
                    .append("' to contain same items, however ")
                    .append(symmetricDifference.size())
                    .append(" items were different: ")
                    .append(symmetricDifference)
                    .append(".")
                    .append(System.getProperty("line.separator"));
                foundError = true;
            }
        }
        return foundError;
    }

    private static boolean findErrorsValueComparison(Object expectedPropertyValue, Object actualPropertyValue, StringBuilder messageBuilder, String currentScope) {
        if(expectedPropertyValue == null || actualPropertyValue == null) {
            return false;
        }
        if (expectedPropertyValue.getClass() != actualPropertyValue.getClass()) {
            messageBuilder.append(currentScope).append("Expected '").append(currentScope).append("' to be of type '")
                    .append(expectedPropertyValue.getClass().getName()).append("' but was of type '")
                    .append(actualPropertyValue.getClass().getName()).append("'.")
                    .append(System.getProperty("line.separator"));
            return true;
        }
        // Only compare if primitives, enum or string
        if (!expectedPropertyValue.equals(actualPropertyValue)) {
            messageBuilder.append("Expected '").append(currentScope)
                    .append("' to be '").append(expectedPropertyValue).append("' but was '").append(actualPropertyValue)
                    .append("'.").append(System.getProperty("line.separator"));
            return true;
        }
        return false;
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }

    private static boolean isPrimitiveOrPrimitiveWrapper(Field field) {
        return isPrimitiveOrPrimitiveWrapper(field.getType());
    }

    private static boolean isPrimitiveOrPrimitiveWrapper(Class<?> field) {
        return field.isPrimitive() || field.isEnum() || field.equals(String.class)||
        field == Double.class || field == Float.class || field == Long.class ||
        field == Integer.class || field == Short.class || field == Character.class ||
        field == Byte.class || field == Boolean.class;
    }

    @SuppressWarnings("DuplicatedCode")
    private static Set<Object> findSymmetricDifference(Iterable firstIterable, Iterable secondIterable) {
        var symmetricDifference = new HashSet<>();
        for(var firstIterableItem : firstIterable) {
                var found = false;
                for(var secondIterableItem : secondIterable) {
                    if(firstIterableItem.equals(secondIterableItem)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    symmetricDifference.add(firstIterableItem);
                }
            }
        for(var secondIterableItem : secondIterable) {
            var found = false;
            for(var firstIterableItem : firstIterable) {
                if(firstIterableItem.equals(secondIterableItem)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                symmetricDifference.add(secondIterableItem);
            }
        }
        return symmetricDifference;
    }
}
