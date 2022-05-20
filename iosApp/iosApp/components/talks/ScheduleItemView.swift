//
//  ScheduleItemView.swift
//  iosApp
//
//  Created by GERARD on 06/02/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ScheduleItemView<TalkItem: View>: View {
    let time: String
    let talks: [TalkItemUi]
    let talkItem: (TalkItemUi) -> TalkItem

    init(time: String, talks: [TalkItemUi], @ViewBuilder talkItem: @escaping (TalkItemUi) -> TalkItem) {
        self.time = time
        self.talks = talks
        self.talkItem = talkItem
    }
    
    var body: some View {
        let space = 60.0
        ZStack(alignment: .topLeading) {
            Text(time)
                .accessibility(label: Text(toSpelloutAccessibleTime(time: time)))
                .font(.body)
                .foregroundColor(Color.c4hSecondary)
                .frame(width: space, alignment: .center)
            VStack(spacing: 4.0) {
                ForEach(talks, id: \.id) { talk in
                    self.talkItem(talk)
                        .padding(.leading, space)
                }
            }
        }
    }
}

func toSpelloutAccessibleTime(time: String) -> String {
    let timeSplitted = time.split(separator: ":")
    return NSLocalizedString("semanticHHmm \(timeSplitted[0]) \(timeSplitted[1])", comment: "")
}

struct ScheduleItemView_Previews: PreviewProvider {
    static var previews: some View {
        ScheduleItemView(
            time: "10:00",
            talks: [
                TalkItemUi.companion.fake,
                TalkItemUi.companion.fake
            ],
            talkItem: { talk in
                TalkItemView(
                    talk: talk,
                    onFavoriteClicked: { TalkItemUi in }
                )
            }
        )
    }
}
