import requests
import json


anilistApiUrl = 'https://graphql.anilist.co'

def getCompletedSeries(name, page):
	query = '''
	query($page: Int, $perPage: Int, $name: String) {
     Page(page: $page, perPage: $perPage) {
         pageInfo {
			total
			perPage
			currentPage
			lastPage
			hasNextPage
         }
    		mediaList(status: COMPLETED, userName:$name, type:ANIME) {
      mediaId,
      media {
        title {
          romaji
          english
          userPreferred
        },
        coverImage {
          extraLarge
          large
          medium
          color
        },
      }
    }
     }
 }
	'''
	# Define our query variables and values that will be used in the query request
	variables = {
		'name': name,
		'page': page,
		'perPage': 50
	}
	# Make the HTTP Api request
	try:
		response = requests.post(anilistApiUrl, json={'query': query, 'variables': variables}).json()
	except:
		print("Couldn't retrieve stats of " + name)
		return -1
	if response['data']['Page']['mediaList'] == None:
		return []
	return response


def getAllCompletedSeries(name):
	series = []
	hasNextPage = True
	i = 1
	while hasNextPage:
		response = getCompletedSeries(name, i)
		series += response['data']['Page']['mediaList']
		hasNextPage = response['data']['Page']['pageInfo']['hasNextPage']
		i += 1
	return series

completedSeries = getAllCompletedSeries("Piede")

with open('completedSeries.json', 'w') as outfile:
	json.dump(completedSeries, outfile)
