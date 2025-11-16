import { Job } from "../../hooks/useJobStorage";
import Card from "./Card";
import { Text } from "react-native";
import { useTheme } from "react-native-paper";
import { renderCardText } from "../../constans/renderCardText";

const renderCard = (item: Job) => {
  const { colors } = useTheme();
  return (
    <Card key={item.id}>
      <Text
        style={{
          fontWeight: "bold",
          fontSize: 18,
          marginBottom: 10,
          color: colors.onSurface,
        }}
      >
        {item.title}
      </Text>
      {renderCardText(item).map((field, idx) => (
        <Text key={idx} style={{ marginVertical: 2, color: colors.onSurface }}>
          <Text style={{ fontWeight: "600" }}>{field.label}:</Text>
          {field.value}
        </Text>
      ))}
    </Card>
  );
};
export default renderCard;
