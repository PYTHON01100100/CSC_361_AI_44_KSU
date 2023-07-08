import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score,precision_score,recall_score,f1_score,classification_report
from sklearn.model_selection import train_test_split
from sklearn import tree



diabetes =  pd.read_csv('diabetes.csv')
d =  pd.read_csv('diabetes.csv',na_values=[0])
d['Outcome'] = diabetes['Outcome']

#Replacing Missing Values with the mean 
d['DiabetesPedigreeFunction'] = d['DiabetesPedigreeFunction'].fillna(value=d['DiabetesPedigreeFunction'].mean())
d['BloodPressure'] = d['BloodPressure'].fillna(value=d['BloodPressure'].mean())
d['SkinThickness'] = d['SkinThickness'].fillna(value=d['SkinThickness'].mean())
d['Pregnancies'] = d['Pregnancies'].fillna(value=d['Pregnancies'].mean())
d['Insulin'] = d['Insulin'].fillna(value=d['Insulin'].mean())
d['Glucose'] = d['Glucose'].fillna(value=d['Glucose'].mean())
d['Age'] = d['Age'].fillna(value=d['Age'].mean())
d['BMI'] = d['BMI'].fillna(value=d['BMI'].mean())
 
X = d.drop(columns=['Outcome'])
Y = d['Outcome']

#Training set (90/10)
X_train1, X_test1, y_train1, y_test1 = train_test_split(X,Y,test_size=0.1)

model1 = DecisionTreeClassifier()
model1.fit(X_train1,y_train1)
predictions1 = model1.predict(X_test1)
print('90/10:\n', predictions1)

accuarcy1 = accuracy_score(y_test1, predictions1)
precision1 =  precision_score(y_test1, predictions1)
recall1 = recall_score(y_test1, predictions1)
f1score1 = f1_score(y_test1, predictions1)
print("Accuarcy:", accuarcy1)
print("Precision:", precision1)
print("Recall:", recall1)
print("F1 score:", f1score1)
print("----------------------------------")



#Training set (80/20)
X_train2, X_test2, y_train2, y_test2 = train_test_split(X,Y,test_size=0.2)

model2 = DecisionTreeClassifier()
model2.fit(X_train2,y_train2)
predictions2 = model2.predict(X_test2)
print('80/20:\n', predictions2)

accuarcy2 = accuracy_score(y_test2, predictions2)
precision2 =  precision_score(y_test2, predictions2)
recall2 = recall_score(y_test2, predictions2)
f1score2 = f1_score(y_test2, predictions2)
print("Accuarcy:", accuarcy2)
print("Precision:", precision2)
print("Recall:", recall2)
print("F1 score:", f1score2)
print("----------------------------------")


#Training set (70/30)
X_train3, X_test3, y_train3, y_test3 = train_test_split(X,Y,test_size=0.3)

model3 = DecisionTreeClassifier()
model3.fit(X_train3,y_train3)
predictions3 = model3.predict(X_test3)
print('70/30:\n', predictions3)

accuarcy3 = accuracy_score(y_test3, predictions3)
precision3=  precision_score(y_test3, predictions3)
recall3 = recall_score(y_test3, predictions3)
f1score3 = f1_score(y_test3, predictions3)
print("Accuarcy:", accuarcy3)
print("Precision:", precision3)
print("Recall:", recall3)
print("F1 score:", f1score3)
print("----------------------------------")


if (accuarcy1>accuarcy2 and accuarcy1>accuarcy3):
    dot_data = tree.export_graphviz(model1, out_file='Diabetes-recommender.dot',
        feature_names=['Pregnancies','Glucose','BloodPressure','SkinThickness','Insulin','BMI','DiabetesPedigreeFunction','Age'],
           class_names=['0','1'],label="all",
           rounded=True,
           filled=True)
    classifier = DecisionTreeClassifier(criterion='entropy', random_state=0)
    classifier.fit(X_train1, y_train1)
    ig = classifier.feature_importances_
    print("Information Gain for Split Node Features:")
    for i, column in enumerate(d.columns[:-1]):
      print(f"{column}: {ig[i]:.4f}")
if (accuarcy2>accuarcy1 and accuarcy2>accuarcy3):
     dot_data = tree.export_graphviz(model2, out_file='Diabetes-recommender.dot',
        feature_names=['Pregnancies','Glucose','BloodPressure','SkinThickness','Insulin','BMI','DiabetesPedigreeFunction','Age'],
           class_names=['0','1'],label="all",
           rounded=True,
           filled=True)
     classifier = DecisionTreeClassifier(criterion='entropy', random_state=0)
     classifier.fit(X_train2, y_train2)
     ig = classifier.feature_importances_
     print("Information Gain for Split Node Features:")
     for i, column in enumerate(d.columns[:-1]):
      print(f"{column}: {ig[i]:.4f}")
if (accuarcy3>accuarcy1 and accuarcy3>accuarcy2):
     dot_data = tree.export_graphviz(model3, out_file='Diabetes-recommender.dot',
        feature_names=['Pregnancies','Glucose','BloodPressure','SkinThickness','Insulin','BMI','DiabetesPedigreeFunction','Age'],
           class_names=['0','1'],label="all",
           rounded=True,
           filled=True)
     classifier = DecisionTreeClassifier(criterion='entropy', random_state=0)
     classifier.fit(X_train3, y_train3)
     ig = classifier.feature_importances_
     print("Information Gain for Split Node Features:")
     for i, column in enumerate(d.columns[:-1]):
      print(f"{column}: {ig[i]:.4f}")
