import WidgetKit
import SwiftUI

private func todayKey() -> String {
  let formatter = DateFormatter()
  formatter.calendar = Calendar.current
  formatter.timeZone = .current
  formatter.locale = Locale(identifier: "en_US_POSIX")
  formatter.dateFormat = "yyyy-MM-dd"
  return formatter.string(from: Date())
}

struct Provider: AppIntentTimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(), configuration: ConfigurationAppIntent())
    }

    func snapshot(for configuration: ConfigurationAppIntent, in context: Context) async -> SimpleEntry {
        SimpleEntry(date: Date(), configuration: configuration)
    }
    
    func timeline(for configuration: ConfigurationAppIntent, in context: Context) async -> Timeline<SimpleEntry> {
        var entries: [SimpleEntry] = []

        // Generate a timeline consisting of five entries an hour apart, starting from the current date.
        let currentDate = Date()
        for hourOffset in 0 ..< 5 {
            let entryDate = Calendar.current.date(byAdding: .hour, value: hourOffset, to: currentDate)!
            let entry = SimpleEntry(date: entryDate, configuration: configuration)
            entries.append(entry)
        }

        return Timeline(entries: entries, policy: .atEnd)
    }

//    func relevances() async -> WidgetRelevances<ConfigurationAppIntent> {
//        // Generate a list containing the contexts this widget is relevant in.
//    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let configuration: ConfigurationAppIntent
}

struct OffersStats: Codable {
  let dayKey: String
  let rejected: Int
  let applied: Int
}

struct widgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
      let defaults = UserDefaults(suiteName: "group.com.majami.job-finder-app")
      let offers: OffersStats = {
        guard
          let rawValue = defaults?.string(forKey: "widget_offers"),
          let data = rawValue.data(using: .utf8),
          let decoded = try? JSONDecoder().decode(OffersStats.self, from: data)
        else {
          return OffersStats(dayKey: todayKey(), rejected: 0, applied: 0)
        }

        guard decoded.dayKey == todayKey() else {
          return OffersStats(dayKey: todayKey(), rejected: 0, applied: 0)
        }

        return decoded
      }()
      VStack(alignment: .leading, spacing: 12) {
        Text("Todays stats:")
          .font(.caption)
          .fontWeight(.semibold)
          .foregroundStyle(.gray)
        VStack(alignment: .leading, spacing: 4) {
          Text("Applied")
            .font(.caption)
            .foregroundStyle(.secondary)
          Text("\(offers.applied)")
            .font(.title2)
            .fontWeight(.semibold)
            .foregroundStyle(.green)
        }
        VStack(alignment: .leading, spacing: 4) {
            Text("Rejected")
              .font(.caption)
              .foregroundStyle(.secondary)
            Text("\(offers.rejected)")
              .font(.title2)
              .fontWeight(.semibold)
              .foregroundStyle(.red)
          }
      }
      .padding()
      .containerBackground(for: .widget) {
        Color.blue.opacity(0.2)
      }
    }
}

struct widget: Widget {
    let kind: String = "widget"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(kind: kind, intent: ConfigurationAppIntent.self, provider: Provider()) { entry in
            widgetEntryView(entry: entry)
                .containerBackground(.fill.tertiary, for: .widget)
        }
    }
}

extension ConfigurationAppIntent {
    fileprivate static var smiley: ConfigurationAppIntent {
        let intent = ConfigurationAppIntent()
        intent.favoriteEmoji = "😀"
        return intent
    }
    
    fileprivate static var starEyes: ConfigurationAppIntent {
        let intent = ConfigurationAppIntent()
        intent.favoriteEmoji = "🤩"
        return intent
    }
}

#Preview(as: .systemSmall) {
    widget()
} timeline: {
    SimpleEntry(date: .now, configuration: .smiley)
    SimpleEntry(date: .now, configuration: .starEyes)
}
